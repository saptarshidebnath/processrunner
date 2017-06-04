/*
 *
 * MIT License
 *
 * Copyright (c) [2016] [Saptarshi Debnath]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.saptarshidebnath.processrunner.lib.process;

import com.saptarshidebnath.processrunner.lib.exception.JsonArrayReaderException;
import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
import com.saptarshidebnath.processrunner.lib.jsonutils.WriteJsonArrayToFile;
import com.saptarshidebnath.processrunner.lib.output.Output;
import com.saptarshidebnath.processrunner.lib.output.OutputFactory;
import com.saptarshidebnath.processrunner.lib.output.OutputRecord;
import com.saptarshidebnath.processrunner.lib.output.OutputSourceType;
import com.saptarshidebnath.processrunner.lib.utilities.Constants;
import com.saptarshidebnath.processrunner.lib.utilities.Utilities;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.saptarshidebnath.processrunner.lib.utilities.Constants.LOGGER_THREAD_COUNT;
import static com.saptarshidebnath.processrunner.lib.utilities.Constants.PROCESS_RUNNER_THREAD_GROUP;

/**
 * Implementation of the {@link ProcessRunner} interface. Gives a solid body to the {@link
 * ProcessRunner}.
 */
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings("COMMAND_INJECTION")
class ProcessRunnerImple implements ProcessRunner {
  private final Configuration configuration;
  private final Runtime runTime;
  private final WriteJsonArrayToFile<OutputRecord> jsonArrayToOutputStream;
  private final Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

  /**
   * Constructor receiving the {@link Configuration} to create the process runner.
   *
   * @param configuration a valid object of {@link Configuration}
   * @throws IOException if Unable to work with the log files
   */
  ProcessRunnerImple(final Configuration configuration) throws IOException {
    this.logger.setLevel(configuration.getLogLevel());
    this.configuration = configuration;
    this.runTime = Runtime.getRuntime();
    if (this.configuration.getMasterLogFile() != null) {
      this.jsonArrayToOutputStream =
          new WriteJsonArrayToFile<>(this.configuration.getMasterLogFile());
    } else {
      this.jsonArrayToOutputStream = null;
    }
    if (this.configuration.getPrintStream() == null) {
      this.logger.log(Level.WARNING, "Streaming option not set. The log will not be streamed.");
    }
    if (this.configuration.getMasterLogFile() == null) {
      this.logger.log(
          Level.WARNING,
          "Master log file not defined in the configuration. No log file will be generated.");
    }
    this.logger.log(Level.INFO, "ProcessRunnerImple created");
  }

  /**
   * Runs the process with the provided configuration in the same {@link Thread}.
   *
   * @return integer value depicting the process exit code
   * @throws ProcessException Throws {@link ProcessException} to denote that some error have
   *     occurred.
   */
  @Override
  public Output run() throws ProcessException {
    final Output output;
    try {
      this.logger.info("Starting process");
      final String commandToExecute =
          Utilities.joinString(
              this.configuration.getInterpreter(),
              Constants.SPACE,
              this.configuration.getCommand());
      this.logger.log(Level.INFO, "Executing command : {0}", commandToExecute);
      //
      // Only if Working dir iis set, extract the working dir.
      //
      File workingDir =
          this.configuration.getWorkingDir() == null
              ? null
              : this.configuration.getWorkingDir().toFile();
      final Process currentProcess =
          this.runTime.exec(commandToExecute.toString(), null, workingDir);
      this.logger.info("Capturing logs");
      //
      // Only create master log file if master log file defined.
      //
      if (this.configuration.getMasterLogFile() != null) {
        this.jsonArrayToOutputStream.startJsonObject();
      }
      //
      // Only try printing / streaming log if both either master log file or streaming option is set.
      //
      if (this.configuration.getMasterLogFile() != null
          || this.configuration.getPrintStream() != null) {
        final ExecutorService executor =
            Executors.newFixedThreadPool(
                LOGGER_THREAD_COUNT,
                runnable ->
                    new Thread(
                        PROCESS_RUNNER_THREAD_GROUP, runnable, "Process runner log-handlers"));
        if (configuration.getPrintStream() != null) {
          configuration.getPrintStream().println("Streaming log output");
        }
        executor.execute(this.writeLogs(currentProcess.getInputStream(), OutputSourceType.SYSOUT));
        executor.execute(
            this.writeLogs(currentProcess.getErrorStream(), OutputSourceType.SYSERROR));
        executor.shutdown();
        this.logger.info("Waiting for the log streams to shutdown");
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
      }
      this.logger.info("Waiting for the process to terminate");
      currentProcess.waitFor();
      if (jsonArrayToOutputStream != null) {
        this.jsonArrayToOutputStream.endJsonObjectWrite();
        this.jsonArrayToOutputStream.cleanup();
      }
      final int processExitValue = currentProcess.exitValue();
      output = OutputFactory.createOutput(this.configuration, processExitValue);
      this.logger.log(Level.INFO, "Process exited with exit value : {0}", processExitValue);
    } catch (final Exception ex) {
      throw new ProcessException(ex);
    }
    return output;
  }

  /**
   * Runs the process with the provided configuration in the seperate {@link Thread}.
   *
   * @return {@link Future} of type {@link Output} reference so that the result of the method
   *     invocation can be retrieved.
   */
  @Override
  public Future<Output> run(final boolean threadEnabledFlag) {
    final ExecutorService executor =
        Executors.newSingleThreadExecutor(
            runnable ->
                new Thread(
                    PROCESS_RUNNER_THREAD_GROUP,
                    runnable,
                    ProcessRunnerImple.this.configuration.toString() + " >> process-runner"));
    final Callable<Output> callable = this::run;
    return executor.submit(callable);
  }

  /**
   * Write a log for the given {@link OutputSourceType} to the provided {@link File}
   *
   * @param targetFile : {@link File} object to where the program should write the log
   * @param outputSourceType : {@link OutputSourceType} to designate type of output
   * @return a {@link File} object
   * @throws ProcessException If unable to log file or unable to read Json array from {@link File}.
   *     You can get the details from {@link ProcessException#getCause()}.
   * @throws JsonArrayReaderException If unable to read Json array {@link JsonArrayReaderException}
   *     from {@link File}
   */

  /**
   * Log a inputStream to the log dump as configured in {@link Configuration}. Internally it calls
   * {@link ProcessRunnerImple#logData(InputStream, OutputSourceType)} to actually log the data and returns a
   * {@link Runnable} reference to make it thread enabled.
   *
   * @param inputStreamToWrite : {@link InputStream} from which the content is being read and
   *     written to a File
   * @param outputSourceType {@link OutputSourceType} depicting the source of the output
   * @return {@link Runnable} instance
   */
  private Runnable writeLogs(
      final InputStream inputStreamToWrite, final OutputSourceType outputSourceType) {
    return () -> logData(inputStreamToWrite, outputSourceType);
  }

  /**
   * Log a inputStream to the log dump as configured in {@link Configuration}.
   *
   * @param inputStreamToWrite : {@link InputStream} from which the content is being read and
   *     written to a File
   * @param outputSourceType {@link OutputSourceType} depicting the source of the output
   */
  private void logData(
      final InputStream inputStreamToWrite, final OutputSourceType outputSourceType) {
    try {
      this.logger.log(
          Level.INFO,
          "Writing {0} as jsonObject to {1}",
          new Object[] {
            outputSourceType.toString(), this.configuration.getMasterLogFile().getCanonicalPath()
          });
      final Scanner scanner = new Scanner(inputStreamToWrite);
      PrintStream printStream = configuration.getPrintStream();
      while (scanner.hasNext()) {
        final String currentLine = scanner.nextLine();
        //        this.logger.log(
        //            Level.INFO, "{0} >> {1}", new Object[] {outputSourceType.toString(), currentLine});
        if (printStream != null) {
          printStream.println(outputSourceType.toString() + " >> " + currentLine);
        }
        if (ProcessRunnerImple.this.jsonArrayToOutputStream != null) {
          ProcessRunnerImple.this.jsonArrayToOutputStream.writeJsonObject(
              new OutputRecord(outputSourceType, currentLine));
        }
      }
    } catch (final Exception ex) {
      final StringWriter sw = new StringWriter();
      ex.printStackTrace(new PrintWriter(sw));
      this.logger.log(
          Level.SEVERE,
          "Unable to write data to {0}",
          new Object[] {this.configuration.getMasterLogFile().getAbsolutePath()});
      this.logger.log(Level.SEVERE, "Cause : {0}", ex);
      this.logger.log(Level.SEVERE, "{0}", new Object[] {sw.toString()});
    }
  }
}
