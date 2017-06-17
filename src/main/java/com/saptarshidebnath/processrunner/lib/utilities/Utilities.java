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

import com.saptarshidebnath.processrunner.lib.exception.JsonArrayReaderException;
import com.saptarshidebnath.processrunner.lib.jsonutils.ReadJsonArrayFromFile;
import com.saptarshidebnath.processrunner.lib.output.OutputRecord;
import com.saptarshidebnath.processrunner.lib.output.OutputSourceType;
import com.saptarshidebnath.processrunner.lib.process.Configuration;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
   * Write a log content from the {@link Configuration#masterLogFile} to new {@link File} as per
   * provided {@link Configuration} for a particular {@link OutputSourceType}.
   *
   * @param configuration accepts a {@link Configuration}
   * @param targetFile accepts a target {@link File}.
   * @param outputSourceType Accepts the {@link OutputSourceType} which need to be printed only.
   * @return a {@link File} reference to the newly written log {@link File}.
   * @throws IOException when there are problems reading and wrting the {@link File}s
   * @throws JsonArrayReaderException when there are issuee reading the Json based master log {@link
   *     File}.
   */
  public static File writeLog(
      final Configuration configuration,
      final File targetFile,
      final OutputSourceType outputSourceType)
      throws IOException, JsonArrayReaderException {
    final Logger logger = Logger.getLogger(Utilities.class.getCanonicalName());
    logger.setLevel(configuration.getLogLevel());
    try (final FileOutputStream fileOutputStream = new FileOutputStream(targetFile, true);
        final PrintWriter printWriter =
            new PrintWriter(new OutputStreamWriter(fileOutputStream, configuration.getCharset()))) {

      final ReadJsonArrayFromFile<OutputRecord> readJsonArrayFromFile =
          new ReadJsonArrayFromFile<>(configuration.getMasterLogFile(), configuration.getCharset());
      logger.log(
          Level.INFO,
          "Writing {0} to : {1}",
          new Object[] {outputSourceType.toString(), targetFile.getCanonicalPath()});
      OutputRecord outputRecord;
      do {
        outputRecord = readJsonArrayFromFile.readNext(OutputRecord.class);
        final String currentOutputLine;
        if (outputRecord != null
            && (outputSourceType == OutputSourceType.ALL
                || outputRecord.getOutputSourceType() == outputSourceType)) {
          currentOutputLine = outputRecord.getOutputText();
          logger.log(
              Level.INFO,
              "{0} >> {1}",
              new Object[] {outputSourceType.toString(), currentOutputLine});
          printWriter.println(currentOutputLine);
        }
      } while (outputRecord != null);
    }

    logger.log(
        Level.INFO,
        "{0} written completely to : {1}",
        new Object[] {outputSourceType.toString(), targetFile.getCanonicalPath()});
    return targetFile;
  }

  public static String joinString(String... stringArray) {
    return joinString(Arrays.asList(stringArray));
  }

  public static String joinString(List<String> stringList) {
    return stringList.stream().collect(Collectors.joining(Constants.EMPTY_STRING));
  }

  public static boolean searchFile(
      Logger logger, File fileToRead, final String regex, Charset charset)
      throws IOException, JsonArrayReaderException {
    boolean isMatching = false;
    logger.log(Level.INFO, "Searching for regular expression : {0}", new Object[] {regex});
    final ReadJsonArrayFromFile<OutputRecord> readJsonArrayFromFile =
        new ReadJsonArrayFromFile<>(fileToRead, charset);
    OutputRecord outputRecord;
    do {
      outputRecord = readJsonArrayFromFile.readNext(OutputRecord.class);
      if (outputRecord != null) {
        isMatching = outputRecord.getOutputText().matches(regex);
      }
    } while (outputRecord != null && !isMatching);
    if (isMatching) {
      logger.log(Level.INFO, "Regex {0} is found", new Object[] {regex});
    } else {
      logger.log(Level.WARNING, "Regex {0} is NOT found", new Object[] {regex});
    }
    readJsonArrayFromFile.closeJsonReader();
    return isMatching;
  }
}
