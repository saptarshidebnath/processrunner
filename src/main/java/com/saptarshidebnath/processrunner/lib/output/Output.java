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
import com.saptarshidebnath.processrunner.lib.process.ProcessRunner;

import java.io.File;
import java.io.IOException;

/**
 * The Output of a run is returned as a reference of {@link Output} class. The interface for the
 * Object which is going to be returned after running {@link ProcessRunner#run()} or {@link
 * ProcessRunner#runAsync()}.
 */
public interface Output {

  /**
   * Prints the {@link OutputRecord#getOutputText()} of type {@link OutputSourceType#SYSOUT} to the
   * {@link File} supplied.
   *
   * @param sysOut A {@link File} object where the log is going to be written.
   * @return A {@link File} object where the log has been written.
   * @throws ProcessException In case of any error. This is a generic error. To get the details,
   *     please use {@link ProcessException#getCause()}.
   */
  File saveSysOut(final File sysOut) throws ProcessException;

  /**
   * Prints the {@link OutputRecord#getOutputText()} of type {@link OutputSourceType#SYSERROR} to
   * the {@link File} supplied.
   *
   * @param sysError A {@link File} object where the log is going to be written.
   * @return A {@link File} object where the log has been written.
   * @throws ProcessException In case of any error. This is a generic error. To get the details,
   *     please use {@link ProcessException#getCause()}.
   */
  File saveSysError(final File sysError) throws ProcessException;

  /**
   * Returns the master log file originally captured while executing the Process. Its an Json Array
   * of type {@link OutputRecord}.
   *
   * @return a {@link File} reference to the json formatted master log .
   */
  File getMasterLogAsJson();

  /**
   * Prints the {@link OutputRecord#getOutputText()} of both {@link OutputSourceType#SYSERROR} and
   * {@link OutputSourceType#SYSOUT} to the {@link File} supplied.
   *
   * @param log A {@link File} object where the log is going to be written.
   * @return A {@link File} object where the log is going to be written.
   * @throws ProcessException is thrown that there are issues regarding the writing of the log or
   *     reading the JSON array.
   * @throws IOException when there is a issue with the IO for reading and writing the file.
   * @throws JsonArrayReaderException when the aster log file in JSON format cannot be read back
   *     from the disk.
   */
  File saveLog(final File log) throws ProcessException, IOException, JsonArrayReaderException;
  /**
   * Returns the process exit / return code.
   *
   * @return return the exit code as an integer value from 0 - 255
   */
  int getReturnCode();

  /**
   * Search the content of the {@link Configuration#getMasterLogFile()} for a particular regex. The
   * search is done line by line.
   *
   * @param regex a proper Regular Expression that need to be searched for.
   * @return a {@link Boolean#TRUE} or {@link Boolean#FALSE} depending upon if the search is
   *     positive or negative.
   * @throws ProcessException In case of any error. This is a generic error. To get the details,
   *     please use {@link ProcessException#getCause()}.
   */
  boolean searchMasterLog(final String regex) throws ProcessException, IOException, JsonArrayReaderException;
}
