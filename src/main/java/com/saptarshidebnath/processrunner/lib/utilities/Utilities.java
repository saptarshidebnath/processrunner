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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class Utilities {

  private static final Logger logger = LoggerFactory.getLogger(Utilities.class);

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
    try (final FileOutputStream fileOutputStream = new FileOutputStream(targetFile, true);
        final PrintWriter printWriter =
            new PrintWriter(new OutputStreamWriter(fileOutputStream, configuration.getCharset()))) {

      final ReadJsonArrayFromFile<OutputRecord> readJsonArrayFromFile =
          new ReadJsonArrayFromFile<>(configuration.getMasterLogFile(), configuration.getCharset());
      logger.info(
          "Writing {} to : {}",
          new Object[] {outputSourceType.toString(), targetFile.getCanonicalPath()});
      OutputRecord outputRecord;
      do {
        outputRecord = readJsonArrayFromFile.readNext(OutputRecord.class);
        final String currentOutputLine;
        if (outputRecord != null
            && (outputSourceType == OutputSourceType.ALL
                || outputRecord.getOutputSourceType() == outputSourceType)) {
          currentOutputLine = outputRecord.getOutputText();
          logger.trace("{} >> {}", new Object[] {outputSourceType.toString(), currentOutputLine});
          printWriter.println(currentOutputLine);
        }
      } while (outputRecord != null);
    }

    logger.info(
        "{} written completely to : {}",
        new Object[] {outputSourceType.toString(), targetFile.getCanonicalPath()});
    return targetFile;
  }

  @SuppressFBWarnings("OPM_OVERLY_PERMISSIVE_METHOD")
  public static String joinString(String... stringArray) {
    return joinString(Arrays.asList(stringArray));
  }

  @SuppressFBWarnings("OPM_OVERLY_PERMISSIVE_METHOD")
  public static String joinString(List<String> stringList) {
    return stringList.stream().collect(Collectors.joining(Constants.EMPTY_STRING));
  }

  public static boolean searchFile(File fileToRead, final String regex, Charset charset)
      throws IOException, JsonArrayReaderException {
    boolean isMatching = false;
    logger.trace("Searching for regular expression : {}", new Object[] {regex});
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
      logger.info("Regex {} is found", new Object[] {regex});
    } else {
      logger.warn("Regex {} is NOT found", new Object[] {regex});
    }
    readJsonArrayFromFile.closeJsonReader();
    return isMatching;
  }

  public static String generateThreadName(Configuration configuration, String suffix) {
    return joinString(configuration.getInterpreter(), configuration.getCommand(), suffix);
  }

  public static List<String> grepFile(File fileToRead, final String regex, Charset charset)
      throws IOException, JsonArrayReaderException {
    List<String> grepedLines = new ArrayList<>();
    logger.trace("Searching for regular expression : {}", new Object[] {regex});
    final ReadJsonArrayFromFile<OutputRecord> readJsonArrayFromFile =
        new ReadJsonArrayFromFile<>(fileToRead, charset);
    OutputRecord outputRecord;
    String text;
    do {
      outputRecord = readJsonArrayFromFile.readNext(OutputRecord.class);
      if (outputRecord != null) {
        text = outputRecord.getOutputText();
        if (text.matches(regex)) {
          grepedLines.add(
              Utilities.joinString(outputRecord.getOutputSourceType().toString(), text));
          logger.trace(Utilities.joinString("Found ", text, " to match ", regex));
        }
      }
    } while (outputRecord != null);
    readJsonArrayFromFile.closeJsonReader();
    return grepedLines;
  }
}
