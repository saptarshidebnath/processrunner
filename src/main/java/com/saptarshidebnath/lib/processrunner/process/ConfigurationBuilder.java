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

package com.saptarshidebnath.lib.processrunner.process;

import com.saptarshidebnath.lib.processrunner.exception.ProcessConfigurationException;
import com.saptarshidebnath.lib.processrunner.utilities.Constants;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ConfigurationBuilder} helps in composing a {@link Configuration}.
 *
 * <p>The {@link Configuration} can be composed using the {@link ConfigurationBuilder}.
 */
public class ConfigurationBuilder {

  private static final Logger logger = LoggerFactory.getLogger(ConfigurationBuilder.class);
  private final String interpreter;
  private final String command;
  private final List<String> comamndParams;
  private Path workingDir;
  private File masterLogFile;
  private boolean autoDeleteFileOnExit;
  private boolean logStreamingEnabled;
  private Charset charset;

  /**
   * The constructor for {@link ConfigurationBuilder}. The only required parameters are the {@link
   * String} interpreter and the {@link String} command.
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
          "Command ProcessRunnerImpl Interpreter is set '"
              + interpreter
              + "'. Need a valid command runner interpreter as /bin/bash in unix");
    } else if (null == command || 0 == command.trim().length()) {
      throw new ProcessConfigurationException(
          "Command is set '" + command + "'. Need a valid command like 'echo Hello World'");
    }
    logger.trace("Interpreter and command validation passed");
    this.interpreter = interpreter;
    this.command = command;
    this.comamndParams = new ArrayList<>();
    this.logStreamingEnabled = false;
  }

  /**
   * Add a {@link String} parameter the the command to be executed.
   *
   * @param param as {@link String}
   * @return the {@link ConfigurationBuilder}
   * @throws ProcessConfigurationException if the parameter is null or an empty String.
   */
  public ConfigurationBuilder setParam(String param) throws ProcessConfigurationException {
    if (param == null || param.length() == 0) {
      throw new ProcessConfigurationException("Param is either null or empty.");
    }
    logger.trace("Param parameter passed validation");
    comamndParams.add(param);
    return this;
  }

  /**
   * Add a {@link List} parameter of type {@link String} to the command to be executed.
   *
   * @param paramList as {@link List} of type {@link String}. The order of the parameters are
   *     maintained.
   * @return the {@link ConfigurationBuilder}
   * @throws ProcessConfigurationException if the parameter is null or an empty String.
   */
  public ConfigurationBuilder setParamList(List<String> paramList)
      throws ProcessConfigurationException {
    if (paramList == null || paramList.isEmpty()) {
      throw new ProcessConfigurationException("Param list is either null or empty.");
    }
    logger.trace("Param list parameter passed validation");
    comamndParams.addAll(paramList);
    return this;
  }

  /**
   * Set the current working Directory as {@link java.nio.file.Path}.
   *
   * @param workingDir accepts {@link Path} current working directory
   * @return the {@link ConfigurationBuilder}
   * @throws ProcessConfigurationException if the working directory is not a directory or it doesn't
   *     exist.
   */
  public ConfigurationBuilder setWorkigDir(Path workingDir) throws ProcessConfigurationException {
    if (!workingDir.toFile().exists() || !workingDir.toFile().isDirectory()) {
      throw new ProcessConfigurationException(
          "Command's current directory is set '"
              + workingDir.toAbsolutePath().toString()
              + "'. Either the Directory doesn't exist or is not a directory at all");
    }
    logger.trace("Working dir parameter passed validation");
    this.workingDir = workingDir;
    return this;
  }

  /**
   * Set the master log file as {@link File}.
   *
   * <p>Master log file is a {@link File} where all the logs are stored by default. If the {@link
   * Configuration#masterLogFile} is not set, the logs are not store anywhere and are discarded.
   *
   * @param masterLogFile accepts a {@link File} reference as master log file.
   * @param autoDeleteFileOnExit accepts a {@link Boolean} flag to determine if the master log file
   *     is going to be auto deleted or not on JVM exit.
   * @return the {@link ConfigurationBuilder}
   * @throws ProcessConfigurationException is thrown if the {@link File} object passed refers to a *
   *     directory or is a read only file
   */
  public ConfigurationBuilder setMasterLogFile(File masterLogFile, boolean autoDeleteFileOnExit)
      throws ProcessConfigurationException {
    return this.setMasterLogFile(masterLogFile, autoDeleteFileOnExit, Constants.UTF_8);
  }

  /**
   * Set the master log file as {@link File} with {@link Boolean} autodelete and {@link Charset}.
   *
   * <p>Master log file is a {@link File} where all the logs are stored by default. If the {@link
   * Configuration#masterLogFile} is not set, the logs are not store anywhere and are discarded.
   *
   * @param masterLogFile accepts a {@link File} reference as master log file.
   * @param autoDeleteFileOnExit accepts a {@link Boolean} flag to determine if the master log file
   *     is going to be auto deleted or not on JVM exit.
   * @param charset a reference of {@link Charset} class to set in which chaset the output file is
   *     going to be written.
   * @return the {@link ConfigurationBuilder}
   * @throws ProcessConfigurationException is thrown if the {@link File} object passed refers to a
   *     directory or is a read only file
   */
  public ConfigurationBuilder setMasterLogFile(
      File masterLogFile, boolean autoDeleteFileOnExit, Charset charset)
      throws ProcessConfigurationException {
    if (masterLogFile.isDirectory()) {
      throw new ProcessConfigurationException(
          "Master log file : "
              + masterLogFile.getAbsolutePath()
              + " is a directory. It must be a writable file");
    } else if (!masterLogFile.canWrite()) {
      throw new ProcessConfigurationException(
          "Master log file : "
              + masterLogFile.getAbsolutePath()
              + " is a readonly. It must be a writable file");
    }

    this.masterLogFile = masterLogFile;
    this.autoDeleteFileOnExit = autoDeleteFileOnExit;
    this.charset = charset;
    return this;
  }

  /**
   * Enable or disable log streaming. Please note that streaming will happen or not depends if the
   * SLF4j finding the logging framework binding and the current log level set for that particular
   * logging framework. All the log streaming will happen in the level equivalent to {@link
   * java.util.logging.Level#INFO}
   *
   * @param logStreamingEnabled a reference of {@link PrintStream}
   * @return the {@link ConfigurationBuilder}
   */
  public ConfigurationBuilder enableLogStreaming(boolean logStreamingEnabled) {
    logger.trace("Setting log streaming as per request");
    this.logStreamingEnabled = logStreamingEnabled;
    return this;
  }

  /**
   * Builds the {@link Configuration} object and returns it back.
   *
   * @return a reference to the {@link Configuration} object created.
   */
  public Configuration build() {
    logger.trace("Building configuration");
    String commandWithParam =
        new StringJoiner(Constants.SPACE_STR)
            .add(command)
            .add(comamndParams.stream().collect(Collectors.joining(Constants.SPACE_STR)))
            .toString();
    logger.trace("Command to be executed : {}", commandWithParam);
    return new Configuration(
        interpreter,
        commandWithParam,
        workingDir,
        masterLogFile,
        charset,
        autoDeleteFileOnExit,
        logStreamingEnabled);
  }

  /**
   * The default generated to String method to print the configuration details.
   *
   * @return a {@link String} implementation of the object {@link ConfigurationBuilder}
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ConfigurationBuilder{");
    sb.append("interpreter='").append(interpreter).append('\'');
    sb.append(", command='").append(command).append('\'');
    sb.append(", comamndParams=").append(comamndParams);
    sb.append(", workingDir=").append(workingDir);
    sb.append(", masterLogFile=").append(masterLogFile);
    sb.append(", autoDeleteFileOnExit=").append(autoDeleteFileOnExit);
    sb.append(", logStreamingEnabled=").append(logStreamingEnabled);
    sb.append(", charset=").append(charset);
    sb.append('}');
    return sb.toString();
  }
}
