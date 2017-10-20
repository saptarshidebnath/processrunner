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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.saptarshidebnath.processrunner.lib.utilities.Constants.UTF_8;

/**
 * {@link ConfigurationBuilder} is the way going forward to build the configuration for the {@link
 * ProcessRunnerFactory} to consume
 */
public class ConfigurationBuilder {
  private static Logger logger = LoggerFactory.getLogger(ConfigurationBuilder.class);
  private final String interpreter;
  private final String command;
  private final ArrayList<String> comamndParams;
  private Path workingDir;
  private File masterLogFile;
  private boolean autoDeleteFileOnExit;
  private boolean logStreamingEnabled;
  private Charset charset;

  /**
   * The only required parameters are the {@link String} interpreter and the {@link String} command
   *
   * @param interpreter as {@link String} for example "bash", "cmd.exe /c"
   * @param command as {@link String} for example "echo 'I solemnly swear I am up to no good'"
   * @throws ProcessConfigurationException is thrown if the interpreter or the command is null or
   *     empty {@link String}
   */
  @SuppressFBWarnings({"SPP_TEMPORARY_TRIM"})
  public ConfigurationBuilder(String interpreter, String command)
      throws ProcessConfigurationException {
    if (interpreter == null || interpreter.trim().length() == 0) {
      throw new ProcessConfigurationException(
          Utilities.joinString(
              "Command ProcessRunnerImpl Interpreter is set '",
              interpreter,
              "'. Need a valid command runner interpreter as /bin/bash in unix"));
    } else if ((command == null) || (command.trim().length() == 0)) {
      throw new ProcessConfigurationException(
          Utilities.joinString(
              "Command is set '", command, "'. Need a valid command like 'echo Hello World'"));
    }
    logger.trace("Interpreter and command validation passed");
    this.interpreter = interpreter;
    this.command = command;
    this.comamndParams = new ArrayList<>();
    this.logStreamingEnabled = false;
  }

  /**
   * Add a {@link String} parameter
   *
   * @param param as {@link String}
   * @return the {@link ConfigurationBuilder}
   * @throws ProcessConfigurationException if the parameter is null or an empty String.
   */
  @SuppressFBWarnings("WEM_WEAK_EXCEPTION_MESSAGING")
  public ConfigurationBuilder setParam(String param) throws ProcessConfigurationException {
    if (param == null || param.length() == 0) {
      throw new ProcessConfigurationException("Param is either null or empty.");
    }
    logger.trace("Param parameter passed validation");
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
  @SuppressFBWarnings("WEM_WEAK_EXCEPTION_MESSAGING")
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
    logger.trace("Working dir parameter passed validation");
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
    logger.trace(
        "Master log file with autodelete parameter received. No immediate validation are going to be made.");
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
  @SuppressFBWarnings("OPM_OVERLY_PERMISSIVE_METHOD")
  public ConfigurationBuilder setMasterLogFile(
      File masterLogFile, boolean autoDeleteFileOnExit, Charset charset) {
    logger.trace(
        "Master log file with autodelete and charset parameter received. No immediate validation are going to be made.");
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
   * Builds the {@link Configuration} object and returns it back
   *
   * @return a reference to the {@link Configuration} object created.
   * @throws IOException on errors while validating paths and log file if any
   * @throws ProcessConfigurationException if there is any exception while populating the {@link
   *     Configuration}
   */
  public Configuration build() throws IOException, ProcessConfigurationException {
    logger.trace("Building configuration");
    String commandWithParam =
        Utilities.joinString(
            command,
            Constants.SPACE_STR,
            comamndParams.stream().collect(Collectors.joining(Constants.SPACE_STR)));
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
      return ReflectionToStringBuilder.toString(this);
  }
}
