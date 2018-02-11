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

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link Configuration} object holds the configuration.
 *
 * <p>The created configuration is used by the {@link ProcessRunner}.
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
   * Constructor to set the configuration to be consumed by {@link ProcessRunner}.
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
    return "Configuration{" + "interpreter='" + interpreter + '\''
        + ", command='" + command + '\''
        + ", workingDir=" + workingDir
        + ", masterLogFile=" + masterLogFile
        + ", charset=" + charset
        + ", autoDeleteFileOnExit=" + autoDeleteFileOnExit
        + ", enableLogStreaming=" + enableLogStreaming
        + '}';
  }
}
