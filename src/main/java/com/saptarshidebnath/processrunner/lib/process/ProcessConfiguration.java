package com.saptarshidebnath.processrunner.lib.process;

import com.saptarshidebnath.processrunner.lib.exception.ProcessConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link ProcessConfiguration} object create a configuration to be consumed by the {@link
 * ProcessRunner} object
 */
public class ProcessConfiguration {
  private final String commandRunnerInterPreter;
  private final String command;
  private final File currentDirectory;
  private final File masterLogFile;
  private final boolean autoDeleteFileOnExit;
  private final Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
  private final Level logLevel;

  /**
   * Most detailed constructor to set the {@link ProcessConfiguration}.
   *
   * @param commandRunnerInterPreter : sets the {@link String} command interpreter like /bin/bash in
   *     unix
   * @param command : set the actual {@link String} command to be executed
   * @param currentDirectory : sets the working directory in {@link File} format
   * @param masterLogFile : {@link File} where the log data will be stored.
   * @param autoDeleteFileOnExit : set the flag to denote if the sysout and the syserror {@link
   *     File} going to be auto deleted on exit
   * @param logLevel {@link Level} value setting for the minimum {@link Level} for printing debug
   *     message.
   * @throws ProcessConfigurationException : Exception thrown if configuration received is not at
   *     par.
   * @throws IOException : Exception thrown if there are any error while validating the {@link File}
   *     objects
   */
  public ProcessConfiguration(
      final String commandRunnerInterPreter,
      final String command,
      final File currentDirectory,
      final File masterLogFile,
      final boolean autoDeleteFileOnExit,
      final Level logLevel)
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
              + currentDirectory.getAbsolutePath()
              + "'. Either the Directory doesn't exist or is not a directory at all");
    } else {
      this.logger.log(Level.INFO, "All parameters passed validation");
    }
    this.commandRunnerInterPreter = commandRunnerInterPreter.trim();
    this.command = command.trim();
    this.currentDirectory = currentDirectory;
    this.autoDeleteFileOnExit = autoDeleteFileOnExit;
    this.masterLogFile = masterLogFile;
    this.logLevel = logLevel;
    this.logger.setLevel(logLevel);
    if (this.autoDeleteFileOnExit) {
      this.masterLogFile.deleteOnExit();
    }
    final String currentConfiguration = toString();
    this.logger.info(currentConfiguration);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ProcessConfiguration{");
    sb.append("commandRunnerInterPreter='").append(this.commandRunnerInterPreter).append('\'');
    sb.append(", command='").append(this.command).append('\'');
    sb.append(", currentDirectory=").append(this.currentDirectory);
    sb.append(", masterLogFile=").append(this.masterLogFile);
    sb.append(", autoDeleteFileOnExit=").append(this.autoDeleteFileOnExit);
    sb.append(", logLevel=").append(this.logLevel);
    sb.append('}');
    return sb.toString();
  }

  public Level getLogLevel() {
    return this.logLevel;
  }

  boolean getAutoDeleteFileOnExit() {
    return this.autoDeleteFileOnExit;
  }

  public File getMasterLogFile() {
    return this.masterLogFile;
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
}
