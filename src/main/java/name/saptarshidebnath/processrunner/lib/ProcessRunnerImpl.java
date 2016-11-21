package name.saptarshidebnath.processrunner.lib;

import name.saptarshidebnath.processrunner.lib.utilities.*;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Implementation of the {@link ProcessRunner} interface */
class ProcessRunnerImpl implements ProcessRunner {
  private final ProcessConfiguration configuration;
  private final Runtime runTime;
  private final WriteJsonArrayToFile<Output> jsonArrayToOutputStream;

  /**
   * Consturctor reciving the {@link ProcessConfiguration} to create the process runner.
   *
   * @param configuration a valid object of {@link ProcessConfiguration}
   * @throws IOException
   */
  ProcessRunnerImpl(final ProcessConfiguration configuration) throws IOException {
    this.configuration = configuration;
    this.runTime = Runtime.getRuntime();
    this.jsonArrayToOutputStream = new WriteJsonArrayToFile<>(this.configuration.getOriginalDump());
  }

  @Override
  public boolean search(final String regex) throws IOException {
    boolean isMatching = false;
    final ReadJsonArrayFromFile<Output> readJsonArrayFromFile =
        new ReadJsonArrayFromFile<>(this.configuration.getOriginalDump());
    Output output = null;
    do {
      output = readJsonArrayFromFile.readNext(Output.class);
      if (output != null) {
        isMatching = output.getOutputText().matches(regex);
      }
    } while (output != null && isMatching == false);
    readJsonArrayFromFile.cleanUp();
    return isMatching;
  }

  public int run() throws IOException, InterruptedException {
    final StringBuilder commandToExecute = new StringBuilder();
    commandToExecute
        .append(this.configuration.getCommandRunnerInterPreter())
        .append(Constants.SPACE)
        .append(this.configuration.getCommand());
    final Process currentProcess =
        this.runTime.exec(
            commandToExecute.toString(), null, this.configuration.getCurrentDirectory());
    this.jsonArrayToOutputStream.startJsonObject();
    final ExecutorService executor = Executors.newFixedThreadPool(2);
    executor.execute(this.logData(currentProcess.getInputStream(), OutputSourceType.SYSOUT));
    executor.execute(this.logData(currentProcess.getErrorStream(), OutputSourceType.SYSERROR));
    executor.shutdown();
    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    currentProcess.waitFor();
    this.jsonArrayToOutputStream.endJsonObjectWrite();
    this.jsonArrayToOutputStream.cleanup();
    this.distributeLog();
    return currentProcess.exitValue();
  }

  public File getSysOut() {
    return this.configuration.getSysOut();
  }

  public File getSysErr() {
    return this.configuration.getSysOut();
  }

  /**
   * Seperate the log into sysout and syserror from the original data dump
   *
   * @throws IOException
   */
  private void distributeLog() throws IOException {
    Output output = null;
    final ReadJsonArrayFromFile<Output> readJsonArrayFromFile =
        new ReadJsonArrayFromFile<>(this.configuration.getOriginalDump());
    final PrintWriter sysOut =
        new PrintWriter(new BufferedWriter(new FileWriter(this.configuration.getSysOut(), true)));
    final PrintWriter sysErr =
        new PrintWriter(new BufferedWriter(new FileWriter(this.configuration.getSysError(), true)));
    do {
      output = readJsonArrayFromFile.readNext(Output.class);
      if (output != null) {
        PrintWriter currentWriter = null;
        if (output.getOutputSourceType() == OutputSourceType.SYSOUT) {
          currentWriter = sysOut;
        } else if (output.getOutputSourceType() == OutputSourceType.SYSERROR) {
          currentWriter = sysErr;
        }
        currentWriter.println(output.getOutputText());
        currentWriter.flush();
      }
    } while (output != null);
    readJsonArrayFromFile.cleanUp();
    sysOut.close();
    sysErr.close();
  }

  /**
   * Log a inputStream to the sysout and syserror as configured in {@link ProcessConfiguration}
   *
   * @param inputStreamToWrite : {@link InputStream} to write to
   * @param outputSourceType {@link OutputSourceType} depicting the source of the output
   * @return {@link Runnable} instance
   * @throws IOException
   */
  private Runnable logData(
      final InputStream inputStreamToWrite, final OutputSourceType outputSourceType)
      throws IOException {
    return () -> {
      final Scanner scanner = new Scanner(inputStreamToWrite);
      while (scanner.hasNext()) {
        final String currentLine = scanner.nextLine();
        if (ProcessRunnerImpl.this.configuration.getAutoDeleteFileOnExit()) {
          if (OutputSourceType.SYSOUT == outputSourceType) {
            System.out.println(currentLine);
          } else if (OutputSourceType.SYSERROR == outputSourceType) {
            System.err.println(currentLine);
          }
        }
        try {
          ProcessRunnerImpl.this.jsonArrayToOutputStream.writeJsonObject(
              new Output(outputSourceType, currentLine));
        } catch (final IOException e) {
          e.printStackTrace();
        }
      }
    };
  }
}
