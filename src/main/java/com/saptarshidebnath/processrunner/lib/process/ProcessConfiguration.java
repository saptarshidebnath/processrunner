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

import com.saptarshidebnath.processrunner.lib.exception.ProcessConfigurationException;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
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
  private final PrintStream printStream;

  /**
   * Constructor to set the configureation to be consumed by {@link ProcessRunner}.
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
    this(
        commandRunnerInterPreter,
        command,
        currentDirectory,
        masterLogFile,
        autoDeleteFileOnExit,
        logLevel,
        null);
  }

  /**
   * Constructor to set the configureation to be consumed by {@link ProcessRunner}.
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
   * @param printStream is a an Object of {@link PrintStream} to which the output of the code can be
   *     streamed at runtime. An example of @{@link PrintStream} object is {@link System#out}
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
      final Level logLevel,
      final PrintStream printStream)
      throws ProcessConfigurationException, IOException {
    this.logLevel = logLevel;
    this.logger.setLevel(logLevel);
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
    if (this.autoDeleteFileOnExit) {
      this.masterLogFile.deleteOnExit();
    }
    final String currentConfiguration = toString();
    this.logger.info(currentConfiguration);
    this.printStream = printStream;
  }

  /**
   * Returns the {@link ProcessConfiguration} as {@link String}. This is mostly used for debug
   * purposes.
   *
   * @return a {@link String}
   */
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

  /**
   * Getter for the current {@link Level} set for the configuration.
   *
   * @return the current {@link Level}
   */
  public Level getLogLevel() {
    return this.logLevel;
  }

  /**
   * Getter for flag if {@link ProcessConfiguration#getMasterLogFile()} is going to auto deleted or
   * not.
   *
   * @return a {@link Boolean} value depicting the same.
   */
  public boolean getAutoDeleteFileOnExit() {
    return this.autoDeleteFileOnExit;
  }

  /**
   * A {@link File} reference for the masterLogFile.
   *
   * @return a {@link File} reference where master logs need to be considered.
   */
  public File getMasterLogFile() {
    return this.masterLogFile;
  }

  /**
   * Get the currently configured command interpreter.
   *
   * @return a {@link String} value.
   */
  public String getCommandRunnerInterPreter() {
    return this.commandRunnerInterPreter;
  }

  /**
   * Get the command / process to be executed.
   *
   * @return a {@link String} value
   */
  public String getCommand() {
    return this.command;
  }

  /**
   * Get the currently configured current directory.
   *
   * @return a {@link File} reference where the current working directory is.
   */
  public File getCurrentDirectory() {
    return this.currentDirectory;
  }

  /**
   * Returns the print writer to stream executed code
   *
   * @return {@link PrintStream} reference where the command output will be streamed on the fly.
   */
  public PrintStream getPrintStream() {
    return this.printStream;
  }
}
