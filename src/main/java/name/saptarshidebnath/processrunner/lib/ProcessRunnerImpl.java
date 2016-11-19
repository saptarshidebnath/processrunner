package name.saptarshidebnath.processrunner.lib;

import name.saptarshidebnath.processrunner.lib.utilities.Constants;
import name.saptarshidebnath.processrunner.lib.utilities.Output;
import name.saptarshidebnath.processrunner.lib.utilities.OutputSource;
import name.saptarshidebnath.processrunner.lib.utilities.WriteJsonArrayToOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ProcessRunnerImpl implements ProcessRunner {
  private final ProcessConfiguration configuration;
  private final Runtime runTime;
  private final WriteJsonArrayToOutputStream<Output> jsonArrayToOutputStream;

  ProcessRunnerImpl(final ProcessConfiguration configuration) throws IOException {
    this.configuration = configuration;
    this.runTime = Runtime.getRuntime();
    this.jsonArrayToOutputStream =
        new WriteJsonArrayToOutputStream<>(
            new FileOutputStream(this.configuration.getOriginalDump(), false),
            StandardCharsets.UTF_8);
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
    this.jsonArrayToOutputStream.beginWriting();
    final ExecutorService executor = Executors.newFixedThreadPool(10);
    executor.execute(this.logData(currentProcess.getInputStream(), OutputSource.SYSOUT));
    executor.execute(this.logData(currentProcess.getErrorStream(), OutputSource.SYSERROR));
    executor.shutdown();
    while (!executor.isShutdown()) {
      System.out.println("Waiting for log capture threads to finish");
    }
    currentProcess.waitFor();
    this.jsonArrayToOutputStream.endWriting();
    return currentProcess.exitValue();
  }

  public File getSysOut() {
    return this.configuration.getSysOut();
  }

  public File getSysErr() {
    return this.configuration.getSysOut();
  }

  public File getOutPut() {
    return null;
  }

  public Runnable logData(final InputStream inputStreamToWrite, final OutputSource outputSource)
      throws IOException {
    System.out.println("Capturing Output Source : " + outputSource);
    return new Runnable() {
      @Override
      public void run() {
        try {
          final Scanner scanner = new Scanner(inputStreamToWrite);
          while (scanner.hasNext()) {
            final String currentLine = scanner.nextLine();
            System.out.println(outputSource + " : " + currentLine);
            ProcessRunnerImpl.this.jsonArrayToOutputStream.writeArrayObject(
                new Output(outputSource, currentLine));
          }
        } catch (final IOException exception) {
          exception.printStackTrace();
        }
      }
    };
  }
}
