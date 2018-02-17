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

package com.saptarshidebnath.lib.processrunner.configuration;

import com.saptarshidebnath.lib.processrunner.constants.ProcessRunnerConstants;
import com.saptarshidebnath.lib.processrunner.exception.ProcessConfigurationException;
import com.saptarshidebnath.lib.processrunner.process.Runner;
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
 * The {@link Configuration} object holds the configuration.
 *
 * <p>The created configuration is used by the {@link Runner}.
 */
public class Configuration {

  private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
  private final String interpreter;
  private final String command;
  private final Path workingDir;
  private final File masterLogFile;
  private final Charset charset;
  private final boolean autoDeleteFileOnExit;
  private final boolean enableLogStreaming;

  /**
   * Constructor to set the configuration to be consumed by {@link Runner}.
   *
   * @param interpreter : sets the {@link String} command interpreter like /bin/bash in unix
   * @param command : set the actual {@link String} command to be executed
   * @param workingDir : sets the working directory in {@link File} format
   * @param masterLogFile : {@link File} where the log data will be stored.
   * @param charset : a reference of which {@link Charset} to use while writing the {@link
   *     Configuration#masterLogFile}
   * @param autoDeleteFileOnExit : set the flag to denote if the sysout and the syserror {@link
   *     File} going to be auto deleted on exit.
   * @param enableLogStreaming : enable lor disable log streaming by passing a @{@link Boolean}
   *     value
   */
  Configuration(
      final String interpreter,
      final String command,
      final Path workingDir,
      final File masterLogFile,
      final Charset charset,
      final boolean autoDeleteFileOnExit,
      final boolean enableLogStreaming) {
    this.interpreter = interpreter.trim();
    this.command = command.trim();
    this.workingDir = workingDir;
    this.autoDeleteFileOnExit = autoDeleteFileOnExit;
    this.masterLogFile = masterLogFile;
    if (this.autoDeleteFileOnExit) {
      this.masterLogFile.deleteOnExit();
    }
    this.enableLogStreaming = enableLogStreaming;
    this.charset = charset;
    logger.debug("Process Runner Configuration : {}", this);
  }

  /**
   * Getter for flag if {@link Configuration#getMasterLogFile()} is going to auto deleted or not.
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
  public String getInterpreter() {
    return this.interpreter;
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
   * @return a {@link Path} reference where the current working directory is.
   */
  public Path getWorkingDir() {
    return this.workingDir;
  }

  /**
   * Returns the charset set for the {@link Configuration#masterLogFile}.
   *
   * @return a reference of the class {@link Charset} in which the {@link
   *     Configuration#masterLogFile} will be written.
   */
  public Charset getCharset() {
    return charset;
  }

  /**
   * Denotes if the master log file is going to be auto deleted when teh JVM exits.
   *
   * @return a {@link Boolean}
   */
  public boolean isAutoDeleteFileOnExit() {
    return autoDeleteFileOnExit;
  }

  /**
   * Returns {@link Boolean#TRUE} or @{@link Boolean#FALSE} to denote if log streaming on the run
   * time is enabled or not.
   *
   * @return a {@link Boolean}
   */
  public boolean isEnableLogStreaming() {
    return enableLogStreaming;
  }

  @Override
  public String toString() {
    return "Configuration{"
        + "interpreter='"
        + interpreter
        + '\''
        + ", command='"
        + command
        + '\''
        + ", workingDir="
        + workingDir
        + ", masterLogFile="
        + masterLogFile
        + ", charset="
        + charset
        + ", autoDeleteFileOnExit="
        + autoDeleteFileOnExit
        + ", enableLogStreaming="
        + enableLogStreaming
        + '}';
  }

  /**
   * {@link ConfigBuilder} helps in composing a {@link Configuration}.
   *
   * <p>The {@link Configuration} can be composed using the {@link ConfigBuilder}.
   */
  public static class ConfigBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ConfigBuilder.class);
    private final String interpreter;
    private final String command;
    private final List<String> comamndParams;
    private Path workingDir;
    private File masterLogFile;
    private boolean autoDeleteFileOnExit;
    private boolean logStreamingEnabled;
    private Charset charset;

    /**
     * The constructor for {@link ConfigBuilder}. The only required parameters are the {@link
     * String} interpreter and the {@link String} command.
     *
     * @param interpreter as {@link String} for example "bash", "cmd.exe /c"
     * @param command as {@link String} for example "echo 'I solemnly swear I am up to no good'"
     * @throws ProcessConfigurationException is thrown if the interpreter or the command is null or
     *     empty {@link String}
     */
    public ConfigBuilder(String interpreter, String command) throws ProcessConfigurationException {
      if (interpreter == null || interpreter.trim().length() == 0) {
        throw new ProcessConfigurationException(
            "Command RunnerImpl Interpreter is set '"
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
     * @return the {@link ConfigBuilder}
     * @throws ProcessConfigurationException if the parameter is null or an empty String.
     */
    public ConfigBuilder setParam(String param) throws ProcessConfigurationException {
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
     * @return the {@link ConfigBuilder}
     * @throws ProcessConfigurationException if the parameter is null or an empty String.
     */
    public ConfigBuilder setParamList(List<String> paramList) throws ProcessConfigurationException {
      if (paramList == null || paramList.isEmpty()) {
        throw new ProcessConfigurationException("Param list is either null or empty.");
      }
      logger.trace("Param list parameter passed validation");
      comamndParams.addAll(paramList);
      return this;
    }

    /**
     * Set the current working Directory as {@link Path}.
     *
     * @param workingDir accepts {@link Path} current working directory
     * @return the {@link ConfigBuilder}
     * @throws ProcessConfigurationException if the working directory is not a directory or it
     *     doesn't exist.
     */
    public ConfigBuilder setWorkigDir(Path workingDir) throws ProcessConfigurationException {
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
     * @param autoDeleteFileOnExit accepts a {@link Boolean} flag to determine if the master log
     *     file is going to be auto deleted or not on JVM exit.
     * @return the {@link ConfigBuilder}
     * @throws ProcessConfigurationException is thrown if the {@link File} object passed refers to a
     *     * directory or is a read only file
     */
    public ConfigBuilder setMasterLogFile(File masterLogFile, boolean autoDeleteFileOnExit)
        throws ProcessConfigurationException {
      return this.setMasterLogFile(
          masterLogFile, autoDeleteFileOnExit, ProcessRunnerConstants.UTF_8);
    }

    /**
     * Set the master log file as {@link File} with {@link Boolean} autodelete and {@link Charset}.
     *
     * <p>Master log file is a {@link File} where all the logs are stored by default. If the {@link
     * Configuration#masterLogFile} is not set, the logs are not store anywhere and are discarded.
     *
     * @param masterLogFile accepts a {@link File} reference as master log file.
     * @param autoDeleteFileOnExit accepts a {@link Boolean} flag to determine if the master log
     *     file is going to be auto deleted or not on JVM exit.
     * @param charset a reference of {@link Charset} class to set in which chaset the output file is
     *     going to be written.
     * @return the {@link ConfigBuilder}
     * @throws ProcessConfigurationException is thrown if the {@link File} object passed refers to a
     *     directory or is a read only file
     */
    public ConfigBuilder setMasterLogFile(
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
     * @return the {@link ConfigBuilder}
     */
    public ConfigBuilder enableLogStreaming(boolean logStreamingEnabled) {
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
          new StringJoiner(ProcessRunnerConstants.SPACE_STR)
              .add(command)
              .add(
                  comamndParams
                      .stream()
                      .collect(Collectors.joining(ProcessRunnerConstants.SPACE_STR)))
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
  }
}
