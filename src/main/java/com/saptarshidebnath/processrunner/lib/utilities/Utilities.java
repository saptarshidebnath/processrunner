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

package com.saptarshidebnath.processrunner.lib.utilities;

import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
import com.saptarshidebnath.processrunner.lib.jsonutils.ReadJsonArrayFromFile;
import com.saptarshidebnath.processrunner.lib.output.OutputRecord;
import com.saptarshidebnath.processrunner.lib.output.OutputSourceType;
import com.saptarshidebnath.processrunner.lib.process.ProcessConfiguration;

import java.io.*;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utilities {

  /** Hidden Constructor. */
  private Utilities() {}

  /**
   * Creates a temporary {@link File}.
   *
   * @return a reference to {@link File}
   * @throws IOException if there are any issues creating the {@link File}
   */
  public static File createTempLogDump() throws IOException {
    return File.createTempFile(Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
  }

  /**
   * Write a log content from the {@link ProcessConfiguration#masterLogFile} to new {@link File} as
   * per provided {@link ProcessConfiguration} for a particular {@link OutputSourceType}.
   *
   * @param configuration accepts a {@link ProcessConfiguration}
   * @param targetFile accepts a target {@link File}.
   * @param outputSourceType Accepts the {@link OutputSourceType} which need to be printed only.
   * @return a {@link File} reference to the newly written log {@link File}.
   * @throws ProcessException in case of any exception.
   */
  public static File writeLog(
      final ProcessConfiguration configuration,
      final File targetFile,
      final OutputSourceType outputSourceType)
      throws ProcessException {
    final Logger logger = Logger.getLogger(Utilities.class.getCanonicalName());
    logger.setLevel(configuration.getLogLevel());
    try (final FileOutputStream fileOutputStream = new FileOutputStream(targetFile, true);
        final PrintWriter printWriter =
            new PrintWriter(new OutputStreamWriter(fileOutputStream, Charset.defaultCharset()))) {
      final ReadJsonArrayFromFile<OutputRecord> readJsonArrayFromFile =
          new ReadJsonArrayFromFile<>(configuration.getMasterLogFile());
      logger.log(
          Level.INFO,
          "Writing {0} to : {1}",
          new Object[] {outputSourceType.toString(), targetFile.getCanonicalPath()});
      OutputRecord outputRecord;
      do {
        outputRecord = readJsonArrayFromFile.readNext(OutputRecord.class);
        final String currentOutputLine;
        if (outputRecord != null && outputRecord.getOutputSourceType() == outputSourceType) {
          currentOutputLine = outputRecord.getOutputText();
          logger.log(
              Level.INFO,
              "{0} >> {1}",
              new Object[] {outputSourceType.toString(), currentOutputLine});
          printWriter.println(currentOutputLine);
        }
      } while (outputRecord != null);

      logger.log(
          Level.INFO,
          "{0} written completely to : {1}",
          new Object[] {outputSourceType.toString(), targetFile.getCanonicalPath()});
    } catch (final Exception e) {
      throw new ProcessException(e);
    }
    return targetFile;
  }
}
