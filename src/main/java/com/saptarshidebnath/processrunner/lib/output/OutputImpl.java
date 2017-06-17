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
import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
import com.saptarshidebnath.processrunner.lib.process.Configuration;
import com.saptarshidebnath.processrunner.lib.utilities.Utilities;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Default Implementation of {@link Output} */
class OutputImpl implements Output {
  private final Configuration configuration;
  private final Logger logger;
  private final int returnCode;

  /**
   * Accepts {@link Configuration} and retunr code to create a {@link Output} object.
   *
   * @param configuration a valid {@link Configuration} object.
   * @param returnCode a {@link Integer} value typically ranging from 0 - 255
   */
  OutputImpl(final Configuration configuration, final int returnCode) {
    this.configuration = configuration;
    this.logger = Logger.getLogger(this.getClass().getCanonicalName());
    this.logger.setLevel(this.configuration.getLogLevel());
    this.returnCode = returnCode;
  }

  /**
   * Prints the {@link OutputRecord#getOutputText()} of type {@link OutputSourceType#SYSOUT} to the
   * {@link File} supplied.
   *
   * @param sysOut A {@link File} object where the log is going to be written.
   * @return A {@link File} object where the log has been written.
   * @throws ProcessException In case of any error. This is a generic error. To get the details,
   *     please use {@link ProcessException#getCause()}.
   */
  @Override
  public File saveSysOut(final File sysOut) throws ProcessException {
    this.logger.log(Level.INFO, "Saving sys out to {0}", new Object[] {sysOut.getAbsolutePath()});
    File response;
    try {
      response = Utilities.writeLog(this.configuration, sysOut, OutputSourceType.SYSOUT);
    } catch (Exception e) {
      throw new ProcessException(e);
    }
    return response;
  }

  /**
   * Prints the {@link OutputRecord#getOutputText()} of type {@link OutputSourceType#SYSERROR} to
   * the {@link File} supplied.
   *
   * @param sysError A {@link File} object where the log is going to be written.
   * @return A {@link File} object where the log has been written.
   * @throws ProcessException In case of any error. This is a generic error. To get the details,
   *     please use {@link ProcessException#getCause()}.
   */
  @Override
  public File saveSysError(final File sysError) throws ProcessException {
    this.logger.log(
        Level.INFO, "Saving sys error to : {0}", new Object[] {sysError.getAbsolutePath()});
    File response = null;
    try {
      response = Utilities.writeLog(this.configuration, sysError, OutputSourceType.SYSERROR);
    } catch (Exception e) {
      throw new ProcessException(e);
    }
    return response;
  }

  /**
   * Returns the master log file originally captured while executing the Process. Its an Json Array
   * of type {@link OutputRecord}.
   *
   * @return a {@link File} reference to the json formatted master log .
   */
  @Override
  public File getMasterLogAsJson() {
    return this.configuration.getMasterLogFile();
  }

  /**
   * Save the log of the process executed as a text file.
   *
   * @param log A {@link File} object where the log is going to be written.
   * @return a reference to the {@link File} where the log is written. Its the same as that of the
   *     parameter taken as input.
   * @throws IOException when there are problems with IO
   * @throws JsonArrayReaderException when there are problems reading the master log file.
   */
  @Override
  public File saveLog(File log) throws IOException, JsonArrayReaderException {
    return Utilities.writeLog(this.configuration, log, OutputSourceType.ALL);
  }

  /**
   * Search the content of the {@link Configuration#getMasterLogFile()} for a particular regex. The
   * search is done line by line.
   *
   * @param regex a proper Regular Expression that need to be searched for.
   * @return a {@link Boolean#TRUE} or {@link Boolean#FALSE} depending upon if the search is
   *     positive or negative.
   * @throws JsonArrayReaderException In case of any error reading the master JSON file . To get the
   *     details, please use {@link ProcessException#getCause()}.
   * @throws IOException In case of any IO error.
   */
  @Override
  public boolean searchMasterLog(final String regex) throws IOException, JsonArrayReaderException {
    return Utilities.searchFile(
        this.logger, this.getMasterLogAsJson(), regex, this.configuration.getCharset());
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
