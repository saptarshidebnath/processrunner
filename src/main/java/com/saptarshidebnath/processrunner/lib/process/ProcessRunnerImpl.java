package com.saptarshidebnath.processrunner.lib.process;

import com.saptarshidebnath.processrunner.lib.exception.JsonArrayReaderException;
import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
import com.saptarshidebnath.processrunner.lib.jsonutils.WriteJsonArrayToFile;
import com.saptarshidebnath.processrunner.lib.output.Output;
import com.saptarshidebnath.processrunner.lib.output.OutputFactory;
import com.saptarshidebnath.processrunner.lib.output.OutputRecord;
import com.saptarshidebnath.processrunner.lib.output.OutputSourceType;
import com.saptarshidebnath.processrunner.lib.utilities.Constants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the {@link ProcessRunner} interface. Gives a solid body to the {@link
 * ProcessRunner}.
 */
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings("COMMAND_INJECTION")
class ProcessRunnerImpl implements ProcessRunner {
  private final ProcessConfiguration configuration;
  private final Runtime runTime;
  private final WriteJsonArrayToFile<OutputRecord> jsonArrayToOutputStream;
  private final Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

  /**
   * Constructor receiving the {@link ProcessConfiguration} to create the process runner.
   *
   * @param configuration a valid object of {@link ProcessConfiguration}
   * @throws IOException if Unable to work with the log files
   */
  ProcessRunnerImpl(final ProcessConfiguration configuration) throws IOException {
    this.configuration = configuration;
    this.runTime = Runtime.getRuntime();
    this.jsonArrayToOutputStream =
        new WriteJsonArrayToFile<>(this.configuration.getMasterLogFile());
    if (this.configuration.isDebug()) {
      this.logger.log(Level.INFO, "Process Runner created");
    }
  }

  /**
   * Runs the process with the provided configuration in the same {@link Thread}.
   *
   * @return integer value depicting the process exit code
   * @throws ProcessException Throws {@link ProcessException} to denote that some error have
   *     occurred.
   * @throws InterruptedException If the thread operations are interrupted.
   */
  @Override
  public Output run() throws ProcessException {
    final Output output;
    try {
      if (this.configuration.isDebug()) {
        this.logger.info("Starting process");
      }
      final StringBuilder commandToExecute = new StringBuilder();
      commandToExecute
          .append(this.configuration.getCommandRunnerInterPreter())
          .append(Constants.SPACE)
          .append(this.configuration.getCommand());
      if (this.configuration.isDebug()) {
        this.logger.info("Executing command : " + commandToExecute.toString());
      }
      final Process currentProcess =
          this.runTime.exec(
              commandToExecute.toString(), null, this.configuration.getCurrentDirectory());
      if (this.configuration.isDebug()) {
        this.logger.info("Capturing logs");
      }
      this.jsonArrayToOutputStream.startJsonObject();
      final ExecutorService executor = Executors.newFixedThreadPool(2);
      executor.execute(this.writeLogs(currentProcess.getInputStream(), OutputSourceType.SYSOUT));
      executor.execute(this.writeLogs(currentProcess.getErrorStream(), OutputSourceType.SYSERROR));
      executor.shutdown();
      if (this.configuration.isDebug()) {
        this.logger.info("Waiting for the log streams to shutdown");
      }
      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
      if (this.configuration.isDebug()) {
        this.logger.info("Waiting for the process to terminate");
      }
      currentProcess.waitFor();
      this.jsonArrayToOutputStream.endJsonObjectWrite();
      this.jsonArrayToOutputStream.cleanup();
      final int processExitValue = currentProcess.exitValue();
      output = OutputFactory.createOutput(this.configuration, processExitValue);
      if (this.configuration.isDebug()) {
        this.logger.info("Process exited with exit value : " + processExitValue);
      }
    } catch (final Exception ex) {
      throw new ProcessException(ex);
    }
    return output;
  }

  /**
   * Runs the process with the provided configuration in the seperate {@link Thread}.
   *
   * @return {@link Future<Integer>} reference so that the result of the method invocation can be
   *     retrieved.
   */
  @Override
  public Future<Output> run(final boolean threadEnabledFlag) {
    final ExecutorService executor = Executors.newSingleThreadExecutor();
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
   * Log a inputStream to the log dump as configured in {@link ProcessConfiguration}. Internally it
   * calls {@link ProcessRunnerImpl#logData(InputStream, OutputSourceType)} to actually log the data
   * and returns a {@link Runnable} reference to make it thread enabled.
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
   * Log a inputStream to the log dump as configured in {@link ProcessConfiguration}.
   *
   * @param inputStreamToWrite : {@link InputStream} from which the content is being read and
   *     written to a File
   * @param outputSourceType {@link OutputSourceType} depicting the source of the output
   */
  private void logData(
      final InputStream inputStreamToWrite, final OutputSourceType outputSourceType) {
    try {
      this.logger.info(
          "Writing "
              + outputSourceType.toString()
              + " as jsonObject to "
              + this.configuration.getMasterLogFile().getCanonicalPath());
      final Scanner scanner = new Scanner(inputStreamToWrite);
      while (scanner.hasNext()) {
        final String currentLine = scanner.nextLine();
        if (this.configuration.isDebug()) {
          this.logger.info(outputSourceType.toString() + " >> " + currentLine);
        }
        ProcessRunnerImpl.this.jsonArrayToOutputStream.writeJsonObject(
            new OutputRecord(outputSourceType, currentLine));
      }
    } catch (final Exception ex) {
      this.logger.log(
          Level.SEVERE,
          "Unable to write data to " + this.configuration.getMasterLogFile().getAbsolutePath(),
          ex);
    }
  }
}
