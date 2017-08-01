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
import com.saptarshidebnath.processrunner.lib.exception.JsonArrayWriterException;
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

import static com.saptarshidebnath.processrunner.lib.utilities.Constants.LOGGER_THREAD_COUNT;
import static com.saptarshidebnath.processrunner.lib.utilities.Constants.PROCESS_RUNNER_THREAD_GROUP;

/**
 * Implementation of the {@link ProcessRunner} interface. Gives a solid body to the {@link
 * ProcessRunner}.
 */
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings("COMMAND_INJECTION")
class ProcessRunnerImpl implements ProcessRunner {
  private final Configuration configuration;
  private final Runtime runTime;
  private final WriteJsonArrayToFile<OutputRecord> jsonArrayToOutputStream;

  /**
   * Constructor receiving the {@link Configuration} to create the process runner.
   *
   * @param configuration a valid object of {@link Configuration}
   * @throws IOException if Unable to work with the log files
   */
  ProcessRunnerImpl(final Configuration configuration) throws IOException {
    this.configuration = configuration;
    this.runTime = Runtime.getRuntime();
    if (this.configuration.getMasterLogFile() != null) {
      this.jsonArrayToOutputStream =
          new WriteJsonArrayToFile<>(
              this.configuration.getMasterLogFile(), this.configuration.getCharset());
    } else {
      this.jsonArrayToOutputStream = null;
    }
    logger.info("Process ProcessRunner created");
  }

  /**
   * Runs the process with the provided configuration in the same {@link Thread}.
   *
   * @return integer value depicting the process exit code
   * @throws ProcessException Throws {@link ProcessException} to denote that some error have
   *     occurred.
   */
  @Override
  public Output run() throws ProcessException, IOException, InterruptedException {
    final Output output;
    this.logger.info("Starting process");
    final StringBuilder commandToExecute = new StringBuilder();
    commandToExecute
        .append(this.configuration.getInterpreter())
        .append(Constants.SPACE)
        .append(this.configuration.getCommand());
    logger.trace("Executing command : {0}", commandToExecute.toString());
    final Process currentProcess =
        this.runTime.exec(
            commandToExecute.toString(),
            null,
            this.configuration.getWorkingDir() == null
                ? null
                : this.configuration.getWorkingDir().toFile());
    logger.trace("Capturing logs");
    if (jsonArrayToOutputStream != null) {
      this.jsonArrayToOutputStream.startJsonObject();
    }
    //
    // If both streaming and logging is disabled, we are not going to bother capturing the log file.
    //
    if (!this.configuration.isEnableLogStreaming()
        && this.configuration.getMasterLogFile() == null) {
      logger.warn("Both output logging and log streaming is disabled or not configured.");
    } else {
      //
      // Capturing log and streaming to log file or to logger or to both
      //
      final ExecutorService executor =
          Executors.newFixedThreadPool(
              LOGGER_THREAD_COUNT,
              runnable ->
                  new Thread(
                      PROCESS_RUNNER_THREAD_GROUP,
                      runnable,
                      ProcessRunnerImpl.this.configuration.toString() + " >> log-handlers"));
      if (configuration.isEnableLogStreaming()) {
        logger.trace("Streaming log output");
      }
      executor.execute(this.writeLogs(currentProcess.getInputStream(), OutputSourceType.SYSOUT));
      executor.execute(this.writeLogs(currentProcess.getErrorStream(), OutputSourceType.SYSERR));
      executor.shutdown();
      logger.trace("Waiting for the log streams to shutdown");
      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }
    logger.info("Waiting for the process to terminate");
    currentProcess.waitFor();
    if (this.jsonArrayToOutputStream != null) {
      this.jsonArrayToOutputStream.endJsonObjectWrite();
      this.jsonArrayToOutputStream.cleanup();
    }
    final int processExitValue = currentProcess.exitValue();
    output = OutputFactory.createOutput(this.configuration, processExitValue);
    logger.info("Process exited with exit value : {0}", processExitValue);
    return output;
  }

  /**
   * Runs the process with the provided configuration in the separate {@link Thread}.
   *
   * @return {@link Future} of type {@link Output} reference so that the result of the method
   *     invocation can be retrieved.
   */
  @Override
  public Future<Output> runAsync() {
    final ExecutorService executor =
        Executors.newSingleThreadExecutor(
            runnable ->
                new Thread(
                    PROCESS_RUNNER_THREAD_GROUP,
                    runnable,
                    ProcessRunnerImpl.this.configuration.toString() + " >> process-runner"));
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
   * {@link ProcessRunnerImpl#logData(InputStream, OutputSourceType)} to actually log the data and
   * returns a {@link Runnable} reference to make it thread enabled.
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
      logger.trace(
          "Writing {0} as jsonObject to {1}",
          new Object[] {
            outputSourceType.toString(), this.configuration.getMasterLogFile().getCanonicalPath()
          });
      final Scanner scanner = new Scanner(inputStreamToWrite);
      while (scanner.hasNext()) {
        final String currentLine = scanner.nextLine();
        if (configuration.isEnableLogStreaming())
          logger.info(Utilities.joinString(outputSourceType.toString(), " >> ", currentLine));
        ProcessRunnerImpl.this.jsonArrayToOutputStream.writeJsonObject(
            new OutputRecord(outputSourceType, currentLine));
      }
    } catch (JsonArrayWriterException | IOException ex) {
      final StringWriter sw = new StringWriter();
      ex.printStackTrace(new PrintWriter(sw));
      logger.error(
          "Unable to log {0}",
          new Object[] {this.configuration.getMasterLogFile().getAbsolutePath()});
      logger.error("Cause : {0}", ex);
      logger.error(sw.toString());
    }
  }
}
