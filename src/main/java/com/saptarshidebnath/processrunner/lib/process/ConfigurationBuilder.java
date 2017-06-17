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
import com.saptarshidebnath.processrunner.lib.utilities.Constants;
import com.saptarshidebnath.processrunner.lib.utilities.Utilities;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.saptarshidebnath.processrunner.lib.utilities.Constants.UTF_8;

/**
 * {@link ConfigurationBuilder} is the way going forward to build the configuration for the {@link
 * ProcessRunnerFactory} to consume
 */
public class ConfigurationBuilder {
  private final String interpreter;
  private final String command;
  private final ArrayList<String> comamndParams;
  private Path workingDir;
  private File masterLogFile;
  private boolean autoDeleteFileOnExit;
  private Logger logger;
  private Level logLevel;
  private PrintStream printStream;
  private Charset charset;

  /**
   * The only required parameters are the {@link String} interpreter and the {@link String} command
   *
   * @param interpreter as {@link String} for example "bash", "cmd.exe /c"
   * @param command as {@link String} for example "echo 'I solemnly swear I am up to no good'"
   * @throws ProcessConfigurationException is thrown if the interpreter or the command is null or
   *     empty {@link String}
   */
  public ConfigurationBuilder(String interpreter, String command)
      throws ProcessConfigurationException {
    if (interpreter == null || interpreter.trim().length() == 0) {
      throw new ProcessConfigurationException(
          Utilities.joinString(
              "Command ProcessRunnerImple Interpreter is set '",
              interpreter,
              "'. Need a valid command runner interpreter as /bin/bash in unix"));
    } else if (command == null || command.trim().length() == 0) {
      throw new ProcessConfigurationException(
          Utilities.joinString(
              "Command is set '", command, "'. Need a valid command like 'echo Hello World'"));
    }
    this.interpreter = interpreter;
    this.command = command;
    this.logLevel = Level.WARNING;
    this.comamndParams = new ArrayList<>();
    logger = Logger.getLogger(this.getClass().getCanonicalName());
  }

  /**
   * Add a {@link String} parameter
   *
   * @param param as {@link String}
   * @return the {@link ConfigurationBuilder}
   * @throws ProcessConfigurationException if the parameter is null or an empty String.
   */
  public ConfigurationBuilder setParam(String param) throws ProcessConfigurationException {
    if (param == null || param.length() == 0) {
      throw new ProcessConfigurationException("Param is either null or empty.");
    }
    comamndParams.add(param);
    return this;
  }

  /**
   * Add a {@link List} parameter of type {@link String}
   *
   * @param paramList as {@link List} of type {@link String}
   * @return the {@link ConfigurationBuilder}
   * @throws ProcessConfigurationException if the parameter is null or an empty String.
   */
  public ConfigurationBuilder setParamList(List<String> paramList)
      throws ProcessConfigurationException {
    if (paramList == null || paramList.size() == 0) {
      throw new ProcessConfigurationException("Param list is either null or empty.");
    }
    comamndParams.addAll(paramList);
    return this;
  }

  /**
   * Set the current working Directory as {@link java.nio.file.Path}
   *
   * @param workingDir accepts {@link Path} current working directory
   * @return the {@link ConfigurationBuilder}
   * @throws ProcessConfigurationException if the working directory is not a directory or it doesn't
   *     exist.
   */
  public ConfigurationBuilder setWorkigDir(Path workingDir) throws ProcessConfigurationException {
    if (!workingDir.toFile().exists() || !workingDir.toFile().isDirectory()) {
      throw new ProcessConfigurationException(
          Utilities.joinString(
              "Command's current directory is set '",
              workingDir.toAbsolutePath().toString(),
              "'. Either the Directory doesn't exist or is not a directory at all"));
    }
    this.workingDir = workingDir;
    return this;
  }

  /**
   * Set the master log file as {@link File}
   *
   * @param masterLogFile accepts a {@link File} reference as master log file.
   * @param autoDeleteFileOnExit accepts a {@link Boolean} flag to determine if the master log file
   *     is going to be auto deleted or not on JVM exit.
   * @return the {@link ConfigurationBuilder}
   */
  public ConfigurationBuilder setMasterLogFile(File masterLogFile, boolean autoDeleteFileOnExit) {
    return this.setMasterLogFile(masterLogFile, autoDeleteFileOnExit, UTF_8);
  }

  /**
   * Set the master log file as {@link File}
   *
   * @param masterLogFile accepts a {@link File} reference as master log file.
   * @param autoDeleteFileOnExit accepts a {@link Boolean} flag to determine if the master log file
   *     is going to be auto deleted or not on JVM exit.
   * @param charset a reference of {@link Charset} class to set in which chaset the output file is
   *     going to be written.
   * @return the {@link ConfigurationBuilder}
   */
  public ConfigurationBuilder setMasterLogFile(
      File masterLogFile, boolean autoDeleteFileOnExit, Charset charset) {
    this.masterLogFile = masterLogFile;
    this.autoDeleteFileOnExit = autoDeleteFileOnExit;
    this.charset = charset;
    return this;
  }

  /**
   * Set the {@link Logger} {@link Level} for the configuration.
   *
   * @param logLevel accepts a {@link Level}
   * @return the {@link ConfigurationBuilder}
   */
  public ConfigurationBuilder setLogLevel(Level logLevel) {
    this.logLevel = logLevel;
    return this;
  }

  /**
   * Set the Streaming destination to a reference of {@link PrintStream}. A very popular example of
   * {@link PrintStream} is {@link System#out}. By setting this configuration the user can live
   * stream the sysout and syserror to the {@link PrintStream}. If the {@link PrintStream} is set to
   * {@link System#out} or {@link System#err} the output the executed stream will be printed to the
   * console live.
   *
   * @param printStream a reference of {@link PrintStream}
   * @return the {@link ConfigurationBuilder}
   */
  public ConfigurationBuilder setStreamingDestination(PrintStream printStream) {
    this.printStream = printStream;
    return this;
  }

  /**
   * Builds the {@link Configuration} object and returns it back
   *
   * @return a reference to the {@link Configuration} object created.
   * @throws IOException on errors while validating paths and log file if any
   * @throws ProcessConfigurationException if there is any exception while populating the {@link
   *     Configuration}
   */
  public Configuration build() throws IOException, ProcessConfigurationException {
    String commandWithParam =
        Utilities.joinString(
            command,
            Constants.SPACE,
            comamndParams
                .stream()
                .map(e -> e + Constants.SPACE)
                .collect(Collectors.joining(Constants.SPACE)));
    Configuration config =
        new Configuration(
            interpreter,
            commandWithParam,
            workingDir,
            masterLogFile,
            charset,
            autoDeleteFileOnExit,
            logLevel,
            printStream);
    this.logger.log(
        Level.INFO,
        Utilities.joinString("Configuration created.", Constants.NEW_LINE, config.toString()));
    return config;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ConfigurationBuilder{");
    sb.append("interpreter='").append(interpreter).append('\'');
    sb.append(", command='").append(command).append('\'');
    sb.append(", comamndParams=").append(comamndParams);
    sb.append(", workingDir=").append(workingDir);
    sb.append(", masterLogFile=").append(masterLogFile);
    sb.append(", autoDeleteFileOnExit=").append(autoDeleteFileOnExit);
    sb.append(", logLevel=").append(logLevel);
    sb.append(", printStream=").append(printStream);
    sb.append('}');
    return sb.toString();
  }
}
