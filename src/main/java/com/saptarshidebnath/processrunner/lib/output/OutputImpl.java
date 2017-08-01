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

package com.saptarshidebnath.processrunner.lib.output;

import com.saptarshidebnath.processrunner.lib.exception.JsonArrayReaderException;
import com.saptarshidebnath.processrunner.lib.exception.ProcessConfigurationException;
import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
import com.saptarshidebnath.processrunner.lib.process.Configuration;
import com.saptarshidebnath.processrunner.lib.utilities.Utilities;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/** Default Implementation of {@link Output} */
class OutputImpl implements Output {
  private final Configuration configuration;
  private final int returnCode;

  /**
   * Accepts {@link Configuration} and retunr code to create a {@link Output} object.
   *
   * @param configuration a valid {@link Configuration} object.
   * @param returnCode a {@link Integer} value typically ranging from 0 - 255
   */
  OutputImpl(final Configuration configuration, final int returnCode) {
    this.configuration = configuration;
    this.returnCode = returnCode;
  }

  /**
   * Prints the {@link OutputRecord#getOutputText()} of type {@link OutputSourceType#SYSOUT} to the
   * {@link File} supplied.
   *
   * @param sysOut A {@link File} object where the log is going to be written.
   * @return A {@link File} object where the log has been written. May return null if master log
   *     file was not configured. Please see {@link
   *     com.saptarshidebnath.processrunner.lib.process.ConfigurationBuilder#setMasterLogFile(File,
   *     boolean)} and {@link
   *     com.saptarshidebnath.processrunner.lib.process.ConfigurationBuilder#setMasterLogFile(File,
   *     boolean, Charset)} for more details on how to set Master log file.
   * @throws ProcessException In case of any error. This is a generic error. To get the details,
   *     please use {@link ProcessException#getCause()}.
   * @throws ProcessConfigurationException if master log is accessed without configuring the same.
   */
  @Override
  public File saveSysOut(final File sysOut) throws ProcessException, ProcessConfigurationException {
    File response = null;
    if (configuration.getMasterLogFile() == null) {
      throw new ProcessConfigurationException(
          "Master log file not configured, cannot save sysout. Please set master log file while configuring the process");
    } else {
      logger.trace("Saving sys out to {0}", new Object[] {sysOut.getAbsolutePath()});
      try {
        response = Utilities.writeLog(this.configuration, sysOut, OutputSourceType.SYSOUT);
      } catch (Exception e) {
        throw new ProcessException(e);
      }
    }
    return response;
  }

  /**
   * Prints the {@link OutputRecord#getOutputText()} of type {@link OutputSourceType#SYSERR} to the
   * {@link File} supplied.
   *
   * @param sysError A {@link File} object where the log is going to be written.
   * @return A {@link File} object where the log has been written. May return null if master log
   *     file was not configured. Please see {@link
   *     com.saptarshidebnath.processrunner.lib.process.ConfigurationBuilder#setMasterLogFile(File,
   *     boolean)} and {@link
   *     com.saptarshidebnath.processrunner.lib.process.ConfigurationBuilder#setMasterLogFile(File,
   *     boolean, Charset)} for more details on how to set Master log file.
   * @throws ProcessException In case of any error. This is a generic error. To get the details,
   *     please use {@link ProcessException#getCause()}.
   * @throws ProcessConfigurationException if master log is accessed without configuring the same.
   */
  @Override
  public File saveSysError(final File sysError)
      throws ProcessException, ProcessConfigurationException {
    File response = null;
    if (configuration.getMasterLogFile() == null) {
      throw new ProcessConfigurationException(
          "Master log file not configured, cannot save syserr. Please set master log file while configuring the process");
    } else {
      this.logger.trace("Saving sys error to : {0}", new Object[] {sysError.getAbsolutePath()});
      try {
        response = Utilities.writeLog(this.configuration, sysError, OutputSourceType.SYSERR);
      } catch (Exception e) {
        throw new ProcessException(e);
      }
    }
    return response;
  }

  /**
   * Returns the master log file originally captured while executing the Process. Its an Json Array
   * of type {@link OutputRecord}.
   *
   * @return A {@link File} master pointing to the Master log file. May return null if master log
   *     file was not configured. Please see {@link
   *     com.saptarshidebnath.processrunner.lib.process.ConfigurationBuilder#setMasterLogFile(File,
   *     boolean)} and {@link
   *     com.saptarshidebnath.processrunner.lib.process.ConfigurationBuilder#setMasterLogFile(File,
   *     boolean, Charset)} for more details on how to set Master log file.
   * @throws ProcessConfigurationException if master log is accessed without configuring the same.
   */
  @Override
  public File getMasterLogAsJson() throws ProcessConfigurationException {
    if (this.configuration.getMasterLogFile() == null) {
      throw new ProcessConfigurationException(
          "Master log file not configured. Please set master log file while configuring the process");
    }
    return this.configuration.getMasterLogFile();
  }

  /**
   * Save the log of the process executed as a text file.
   *
   * @param log A {@link File} object where the log is going to be written.
   * @return A {@link File} the log not in json format. Please see {@link
   *     com.saptarshidebnath.processrunner.lib.process.ConfigurationBuilder#setMasterLogFile(File,
   *     boolean)} and {@link
   *     com.saptarshidebnath.processrunner.lib.process.ConfigurationBuilder#setMasterLogFile(File,
   *     boolean, Charset)} for more details on how to set Master log file.
   * @throws IOException when there are problems with IO
   * @throws JsonArrayReaderException when there are problems reading the master log file.
   * @throws ProcessConfigurationException if master log is accessed without configuring the same.
   */
  @Override
  public File saveLog(File log)
      throws IOException, JsonArrayReaderException, ProcessConfigurationException {
    File response = null;
    if (configuration.getMasterLogFile() == null) {
      throw new ProcessConfigurationException(
          "Master log file not configured, cannot save log. Please set master log file while configuring the process");
    } else {
      response = Utilities.writeLog(this.configuration, log, OutputSourceType.ALL);
    }
    return response;
  }

  /**
   * Search the content of the {@link Configuration#getMasterLogFile()} for a particular regex. The
   * search is done line by line.
   *
   * @param regex a proper Regular Expression that need to be searched for.
   * @return a {@link Boolean#TRUE} or {@link Boolean#FALSE} depending upon if the search is
   *     positive or negative. Please see {@link
   *     com.saptarshidebnath.processrunner.lib.process.ConfigurationBuilder#setMasterLogFile(File,
   *     boolean)} and {@link
   *     com.saptarshidebnath.processrunner.lib.process.ConfigurationBuilder#setMasterLogFile(File,
   *     boolean, Charset)} for more details on how to set Master log file.
   * @throws JsonArrayReaderException In case of any error reading the master JSON file . To get the
   *     details, please use {@link ProcessException#getCause()}.
   * @throws IOException In case of any IO error.
   * @throws ProcessConfigurationException if master log is accessed without configuring the same.
   */
  @Override
  public boolean searchMasterLog(final String regex)
      throws IOException, JsonArrayReaderException, ProcessConfigurationException {
    Boolean response = null;
    if (configuration.getMasterLogFile() == null) {
      String message =
          "Master log file not configured, cannot search log. Please set master log file while configuring the process";
      throw new ProcessConfigurationException(message);
    } else {
      response =
          Utilities.searchFile(
              configuration.getMasterLogFile(), regex, this.configuration.getCharset());
    }
    return response;
  }

  /**
   * Searches the file for the regular expression and returns a {@link List} of type {@link String}
   * containing all the matching lines.
   *
   * @param regex accepts a {@link String} object to search for
   * @return a {@link List} of String containing all the lines in the output that have matched the
   *     regex
   * @throws IOException if there are any error reading the master log file.
   * @throws JsonArrayReaderException if there are any error reading the master log file
   * @throws ProcessConfigurationException if master log file not configured to be saved when the
   *     process is started and later user is trying to grep for non existent file.
   */
  @Override
  public List<String> grepForRegex(String regex)
      throws IOException, JsonArrayReaderException, ProcessConfigurationException {
    List<String> response = null;
    if (configuration.getMasterLogFile() == null) {
      String message =
          "Master log file not configured, cannot search log. Please set master log file while configuring the process";
      throw new ProcessConfigurationException(message);
    } else {
      response =
          Utilities.grepFile(configuration.getMasterLogFile(), regex, configuration.getCharset());
    }
    return response;
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
}
