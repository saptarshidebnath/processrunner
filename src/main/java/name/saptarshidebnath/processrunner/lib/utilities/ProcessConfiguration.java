package name.saptarshidebnath.processrunner.lib.utilities;

import name.saptarshidebnath.processrunner.lib.exception.ProcessConfigurationException;

import java.io.File;
import java.io.IOException;

/** Created by saptarshi on 11/17/2016. */
public class ProcessConfiguration {
  private final String commandRunnerInterPreter;
  private final String command;
  private final File currentDirectory;
  private final File sysOut;
  private final File sysError;

  /**
   * Process configuration with default value for {@link File} sysout and {@link File} syserror.
   * Both the files are marked to be auto deleted at system exit.
   *
   * @param commandRunnerInterPreter : sets the {@link String} command interpreter like /bin/bash in
   *     unix
   * @param command : set the actual {@link String} command to be executed
   * @param currentDirectory : sets the working directory in {@link File} format
   * @throws ProcessConfigurationException : Exception thrown if configuration received is not at
   *     par.
   * @throws IOException : Exception thrown if there are any error while validating the {@link File}
   *     objects
   */
  public ProcessConfiguration(
      final String commandRunnerInterPreter, final String command, final File currentDirectory)
      throws IOException, ProcessConfigurationException {
    this(
        commandRunnerInterPreter,
        command,
        currentDirectory,
        File.createTempFile("ProcessRunner-sysout-", ".txt"),
        File.createTempFile("ProcessRunner-syserr-", ".txt"),
        true);
  }

  /**
   * Most detailed constructor to set the {@link ProcessConfiguration}.
   *
   * @param commandRunnerInterPreter : sets the {@link String} command interpreter like /bin/bash in
   *     unix
   * @param command : set the actual {@link String} command to be executed
   * @param currentDirectory : sets the working directory in {@link File} format
   * @param sysOut : sets the {@link File} where the System out are going to be written
   * @param sysError : sets the {@link File} where the System err are going to be written
   * @param autoDeleteFileOnExit : set the flag to denote if the sysout and the syserror {@link
   *     File} going to be auto deleted on exit
   * @throws ProcessConfigurationException : Exception thrown if configuration received is not at
   *     par.
   * @throws IOException : Exception thrown if there are any error while validating the {@link File}
   *     objects
   */
  public ProcessConfiguration(
      final String commandRunnerInterPreter,
      final String command,
      final File currentDirectory,
      final File sysOut,
      final File sysError,
      final boolean autoDeleteFileOnExit)
      throws ProcessConfigurationException, IOException {

    if (commandRunnerInterPreter.trim().length() == 0) {
      throw new ProcessConfigurationException(
          "Command Runner Interpreter is set '"
              + commandRunnerInterPreter
              + "'. Need a valid command runner interpreter as /bin/bash in unix");
    } else if (command.trim().length() == 0) {
      throw new ProcessConfigurationException(
          "Command is set '" + command + "'. Need a valid command like 'echo Hello World'");
    } else if (!currentDirectory.exists() || !currentDirectory.isDirectory()) {
      throw new ProcessConfigurationException(
          "Command's current directory is set '"
              + currentDirectory.getCanonicalPath()
              + "'. Either the Directory doesn't exist or is not a directory at all");
    } else if (sysOut.exists()) {
      throw new ProcessConfigurationException(
          "File '"
              + sysOut.getCanonicalPath()
              + "' already exists. Please provide a file path that is not created yet for sysout.");
    } else if (sysError.exists()) {
      throw new ProcessConfigurationException(
          "File '"
              + sysError.getCanonicalPath()
              + "' already exists. Please provide a file path that is not created yet for syserr.");
    }
    this.commandRunnerInterPreter = commandRunnerInterPreter.trim();
    this.command = command.trim();
    this.currentDirectory = currentDirectory;
    this.sysOut = sysOut;
    this.sysError = sysError;
    sysOut.createNewFile();
    sysError.createNewFile();
    if (autoDeleteFileOnExit) {
      sysOut.deleteOnExit();
      sysError.deleteOnExit();
    }
  }

  public String getCommandRunnerInterPreter() {
    return this.commandRunnerInterPreter;
  }

  public String getCommand() {
    return this.command;
  }

  public File getCurrentDirectory() {
    return this.currentDirectory;
  }

  public File getSysOut() {
    return this.sysOut;
  }

  public File getSysError() {
    return this.sysError;
  }
}
