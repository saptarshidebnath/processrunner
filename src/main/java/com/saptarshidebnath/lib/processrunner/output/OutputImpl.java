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

package com.saptarshidebnath.lib.processrunner.output;

import com.saptarshidebnath.lib.processrunner.exception.ProcessConfigurationException;
import com.saptarshidebnath.lib.processrunner.process.Configuration;
import com.saptarshidebnath.lib.processrunner.process.ConfigurationBuilder;
import com.saptarshidebnath.lib.processrunner.utilities.Constants;
import com.saptarshidebnath.lib.processrunner.utilities.fileutils.GrepFile;
import com.saptarshidebnath.lib.processrunner.utilities.fileutils.LogWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default Implementation of {@link Output}.
 *
 * <p>If the masterlog file is configured, the class gives the ability to :-
 *
 * <ul>
 *   <li>Save the {@link OutputSourceType#SYSOUT} to a file.
 *   <li>Save the {@link OutputSourceType#SYSERR} to a file.
 *   <li>Save both {@link OutputSourceType#ALL} to file.
 *   <li>Search the master log file for a regular expression pattern.
 *   <li>get the return code and the actual log file if saved while executing the program.
 * </ul>
 */
class OutputImpl implements Output {
  private static Logger logger = LoggerFactory.getLogger(Output.class);
  private final Configuration configuration;
  private final int returnCode;

  /**
   * Accepts {@link Configuration} and return code to create a {@link Output} object.
   *
   * @param configuration a valid {@link Configuration} object.
   * @param returnCode a {@link Integer} value typically ranging from 0 - 255
   */
  OutputImpl(final Configuration configuration, final int returnCode) {
    this.configuration = configuration;
    this.returnCode = returnCode;
  }

  @Override
  public String toString() {
    return "OutputImpl{" + "configuration=" + configuration + ", returnCode=" + returnCode + '}';
  }

  /**
   * Prints the {@link OutputRecord#getOutputText()} of type {@link OutputSourceType#SYSOUT} to the
   * {@link File} supplied.
   *
   * @param sysOut A {@link File} object where the log is going to be written.
   * @return A {@link File} object where the log has been written. May return null if master log
   *     file was not configured. Please see {@link ConfigurationBuilder#setMasterLogFile(File,
   *     boolean)} and {@link ConfigurationBuilder#setMasterLogFile(File, boolean, Charset)} for
   *     more details on how to set Master log file.
   * @throws IOException In case of any IO Error while writing to disk.
   * @throws ProcessConfigurationException if master log is accessed without configuring the same.
   */
  @Override
  public File saveSysOut(final File sysOut) throws ProcessConfigurationException, IOException {
    File response;
    if (configuration.getMasterLogFile() == null) {
      throw new ProcessConfigurationException(
          Constants.processConfigExceptionTextMasterLogFileNotConfigured + configuration);
    }
    logger.trace("Saving sys out to {}", sysOut.getAbsolutePath());
    response = new LogWriter().writeLog(this.configuration, sysOut, OutputSourceType.SYSOUT);
    return response;
  }

  /**
   * Prints the {@link OutputRecord#getOutputText()} of type {@link OutputSourceType#SYSERR} to the
   * {@link File} supplied.
   *
   * @param sysError A {@link File} object where the log is going to be written.
   * @return A {@link File} object where the log has been written. May return null if master log
   *     file was not configured. Please see {@link ConfigurationBuilder#setMasterLogFile(File,
   *     boolean)} and {@link ConfigurationBuilder#setMasterLogFile(File, boolean, Charset)} for
   *     more details on how to set Master log file.
   * @throws IOException In case of any IO Error while writing to disk.
   * @throws ProcessConfigurationException if master log is accessed without configuring the same.
   */
  @Override
  public File saveSysError(final File sysError) throws ProcessConfigurationException, IOException {
    File response;
    if (configuration.getMasterLogFile() == null) {
      throw new ProcessConfigurationException(
          Constants.processConfigExceptionTextMasterLogFileNotConfigured + configuration);
    } else {
      logger.trace("Saving sys error to : {}", new Object[] {sysError.getAbsolutePath()});
      response = new LogWriter().writeLog(this.configuration, sysError, OutputSourceType.SYSERR);
    }
    return response;
  }

  /**
   * Returns the master log file originally captured while executing the Process. Its an Json Array
   * of type {@link OutputRecord}.
   *
   * @return A {@link File} master pointing to the Master log file. May return null if master log
   *     file was not configured. Please see {@link ConfigurationBuilder#setMasterLogFile(File,
   *     boolean)} and {@link ConfigurationBuilder#setMasterLogFile(File, boolean, Charset)} for
   *     more details on how to set Master log file.
   * @throws ProcessConfigurationException if master log is accessed without configuring the same.
   */
  @Override
  public File getMasterLogAsJson() throws ProcessConfigurationException {
    if (this.configuration.getMasterLogFile() == null) {
      throw new ProcessConfigurationException(
          Constants.processConfigExceptionTextMasterLogFileNotConfigured + configuration);
    }
    return this.configuration.getMasterLogFile();
  }

  /**
   * Save the log of the process executed as a text file.
   *
   * @param log A {@link File} object where the log is going to be written.
   * @return A {@link File} the log not in json format. Please see {@link
   *     ConfigurationBuilder#setMasterLogFile(File, boolean)} and {@link
   *     ConfigurationBuilder#setMasterLogFile(File, boolean, Charset)} for more details on how to
   *     set Master log file.
   * @throws IOException when there are problems with IO
   * @throws ProcessConfigurationException if master log is accessed without configuring the same.
   */
  @Override
  public File saveLog(File log) throws IOException, ProcessConfigurationException {
    if (configuration.getMasterLogFile() == null) {
      throw new ProcessConfigurationException(
          Constants.processConfigExceptionTextMasterLogFileNotConfigured + configuration);
    }
    return new LogWriter().writeLog(this.configuration, log, OutputSourceType.ALL);
  }

  /**
   * Search the content of the {@link Configuration#getMasterLogFile()} for a particular regex. The
   * search is done line by line.
   *
   * @param regex a proper Regular Expression that need to be searched for.
   * @return a {@link Boolean#TRUE} or {@link Boolean#FALSE} depending upon if the search is
   *     positive or negative. Please see {@link ConfigurationBuilder#setMasterLogFile(File,
   *     boolean)} and {@link ConfigurationBuilder#setMasterLogFile(File, boolean, Charset)} for
   *     more details on how to set Master log file.
   * @throws IOException In case of any IO error.
   * @throws ProcessConfigurationException if master log is accessed without configuring the same.
   */
  @Override
  public boolean searchMasterLog(final String regex)
      throws IOException, ProcessConfigurationException {
    Boolean response;
    if (configuration.getMasterLogFile() == null) {
      String message =
          Constants.processConfigExceptionTextMasterLogFileNotConfigured + configuration;
      throw new ProcessConfigurationException(message);
    } else {
      response =
          this.searchFile(configuration.getMasterLogFile(), regex, this.configuration.getCharset());
    }
    return response;
  }

  /**
   * Searches the file for the regular expression and returns a {@link List} of type {@link String}
   * containing all the matching lines.
   *
   * @param regex accepts a {@link String} object to search for
   * @return a {@link List} of {@link OutputRecord} containing all the lines in the output that have
   *     matched the regex
   * @throws IOException if there are any error reading the master log file.
   * @throws ProcessConfigurationException if master log file not configured to be saved when the
   *     process is started and later user is trying to grep for non existent file.
   */
  @Override
  public List<OutputRecord> grepForRegex(String regex)
      throws IOException, ProcessConfigurationException {
    if (configuration.getMasterLogFile() == null) {
      String message =
          Constants.processConfigExceptionTextMasterLogFileNotConfigured + configuration;
      throw new ProcessConfigurationException(message);
    }
    return new GrepFile().grepFile(regex, configuration);
  }

  /**
   * Returns the process exit / return code.
   *
   * @return return the exit code as an integer value from 0 - 255
   */
  @Override
  public int getReturnCode() {
    return this.returnCode;
  }

  private boolean searchFile(File fileToRead, final String regex, Charset charset)
      throws IOException {
    logger.trace("Searching for regular expression : {}", regex);
    try (Stream<String> stream = Files.lines(Paths.get(fileToRead.getCanonicalPath()), charset)) {
      return stream
          .parallel()
          .map(currentLine -> Constants.GSON.fromJson(currentLine, OutputRecord.class))
          .anyMatch(outputRecord -> outputRecord.getOutputText().matches(regex));
    }
  }
}
