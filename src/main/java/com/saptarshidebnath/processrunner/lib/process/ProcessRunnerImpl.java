package com.saptarshidebnath.processrunner.lib.process;

import com.saptarshidebnath.processrunner.lib.exception.JsonArrayReaderException;
import com.saptarshidebnath.processrunner.lib.exception.JsonArrayWriterException;
import com.saptarshidebnath.processrunner.lib.jsonutils.ReadJsonArrayFromFile;
import com.saptarshidebnath.processrunner.lib.jsonutils.WriteJsonArrayToFile;
import com.saptarshidebnath.processrunner.lib.output.Output;
import com.saptarshidebnath.processrunner.lib.output.OutputSourceType;
import com.saptarshidebnath.processrunner.lib.utilities.Constants;

import java.io.*;
import java.nio.charset.Charset;
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
  private final WriteJsonArrayToFile<Output> jsonArrayToOutputStream;
  private final Logger logger= Logger.getLogger(this.getClass().getCanonicalName());


  /**
   * Constructor receiving the {@link ProcessConfiguration} to create the process runner.
   *
   * @param configuration a valid object of {@link ProcessConfiguration}
   * @throws IOException if Unable to work with the log files
   */
  ProcessRunnerImpl(final ProcessConfiguration configuration) throws IOException {
    this.configuration = configuration;
    this.runTime = Runtime.getRuntime();
    this.jsonArrayToOutputStream = new WriteJsonArrayToFile<>(this.configuration.getLogDump());
    this.logger.log(Level.INFO, "Process Runner created");
  }

  @Override
  public boolean search(final String regex) throws IOException, JsonArrayReaderException {
    this.logger.info("Searching for regular expression :" + regex);
    boolean isMatching = false;
    final ReadJsonArrayFromFile<Output> readJsonArrayFromFile =
        new ReadJsonArrayFromFile<>(this.configuration.getLogDump());
    Output output;
    do {
      output = readJsonArrayFromFile.readNext(Output.class);
      if (output != null) {
        isMatching = output.getOutputText().matches(regex);
      }
    } while (output != null && !isMatching);
    if (isMatching) {
      this.logger.info("Regex \'" + regex + "\" is found");
    } else {
      this.logger.info("Regex \'" + regex + "\" NOT found");
    }
    readJsonArrayFromFile.closeJsonReader();
    return isMatching;
  }

  /**
   * Runs the process with the provided configuration in the same {@link Thread}.
   *
   * @return integer value depicting the process exit code
   * @throws IOException If unable to work with File.
   * @throws InterruptedException If the thread operations are interrupted.
   */
  @Override
  public Integer run() throws IOException, InterruptedException {
    this.logger.info("Starting process");
    final StringBuilder commandToExecute = new StringBuilder();
    commandToExecute
        .append(this.configuration.getCommandRunnerInterPreter())
        .append(Constants.SPACE)
        .append(this.configuration.getCommand());
    this.logger.info("Executing command : " + commandToExecute.toString());
    final Process currentProcess =
        this.runTime.exec(
            commandToExecute.toString(), null, this.configuration.getCurrentDirectory());
    this.logger.info("Capturing logs");
    this.jsonArrayToOutputStream.startJsonObject();
    final ExecutorService executor = Executors.newFixedThreadPool(2);
    executor.execute(this.writeLogs(currentProcess.getInputStream(), OutputSourceType.SYSOUT));
    executor.execute(this.writeLogs(currentProcess.getErrorStream(), OutputSourceType.SYSERROR));
    executor.shutdown();
    this.logger.info("Waiting for the log streams to shutdown");
    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    this.logger.info("Waiting for the process to terminate");
    currentProcess.waitFor();
    this.jsonArrayToOutputStream.endJsonObjectWrite();
    this.jsonArrayToOutputStream.cleanup();
    final int processExitValue = currentProcess.exitValue();
    this.logger.info("Process exited with exit value : " + processExitValue);
    return processExitValue;
  }

  /**
   * Runs the process with the provided configuration in the seperate {@link Thread}.
   *
   * @return {@link Future<Integer>} reference so that the result of the method invocation can be
   *     retrieved.
   */
  @Override
  public Future<Integer> run(final boolean threadEnabledFlag) {
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    final Callable<Integer> callable = this::run;
    return executor.submit(callable);
  }

  @Override
  public File saveSysOut(final File sysOut) throws IOException, JsonArrayReaderException {
    this.logger.info("Saving sys out to " + sysOut.getCanonicalPath());
    return this.writeLog(sysOut, OutputSourceType.SYSOUT);
  }

  @Override
  public File saveSysError(final File sysError) throws IOException, JsonArrayReaderException {
    this.logger.info("Saving sys error to : " + sysError.getCanonicalPath());
    return this.writeLog(sysError, OutputSourceType.SYSERROR);
  }

  @Override
  public File getJsonLogDump() {
    return this.configuration.getLogDump();
  }

  /**
   * Write a log for the given {@link OutputSourceType} to the provided {@link File}
   *
   * @param targetFile : {@link File} object to where the program should write the log
   * @param outputSourceType : {@link OutputSourceType} to designate type of output
   * @return a {@link File} object
   * @throws IOException If unable to log file.
   * @throws JsonArrayReaderException If unable to read Json array {@link JsonArrayReaderException}
   *     from {@link File}
   */
  private File writeLog(final File targetFile, final OutputSourceType outputSourceType)
      throws JsonArrayReaderException, IOException {
    final ReadJsonArrayFromFile<Output> readJsonArrayFromFile =
        new ReadJsonArrayFromFile<>(this.configuration.getLogDump());
    final FileOutputStream fileOutputStream = new FileOutputStream(targetFile, true);
    final PrintWriter printWriter =
        new PrintWriter(new OutputStreamWriter(fileOutputStream, Charset.defaultCharset()));
    try {
      this.logger.info(
          "Writing " + outputSourceType.toString() + " to : " + targetFile.getCanonicalPath());
      Output output;
      do {
        output = readJsonArrayFromFile.readNext(Output.class);
        final String currentOutputLine;
        if (output != null && output.getOutputSourceType() == outputSourceType) {
          currentOutputLine = output.getOutputText();
          this.logger.info(outputSourceType.toString() + " >> " + currentOutputLine);
          printWriter.println(currentOutputLine);
        }
      } while (output != null);

      this.logger.info(
          outputSourceType.toString()
              + " written completely to : "
              + targetFile.getCanonicalPath());
    } finally {
      fileOutputStream.flush();
      fileOutputStream.close();
      printWriter.close();
      printWriter.flush();
    }
    return targetFile;
  }

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
              + this.configuration.getLogDump().getCanonicalPath());
      final Scanner scanner = new Scanner(inputStreamToWrite);
      while (scanner.hasNext()) {
        final String currentLine = scanner.nextLine();
        this.logger.info(outputSourceType.toString() + " >> " + currentLine);
        ProcessRunnerImpl.this.jsonArrayToOutputStream.writeJsonObject(
            new Output(outputSourceType, currentLine));
      }
    } catch (final IOException | JsonArrayWriterException ex) {
      this.logger.log(
          Level.SEVERE,
          "Unable to write data to " + this.configuration.getLogDump().getAbsolutePath(),
          ex);
    }
  }
}
