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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saptarshidebnath.processrunner.lib.output.OutputRecord;
import com.saptarshidebnath.processrunner.lib.output.OutputSourceType;
import com.saptarshidebnath.processrunner.lib.process.Configuration;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Utilities {

  public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
   * @throws IOException when there are problems reading and wrting the {@link File}.
   */
  public static File writeLog(
      final Configuration configuration,
      final File targetFile,
      final OutputSourceType outputSourceType)
      throws IOException {
    try (final FileOutputStream fileOutputStream = new FileOutputStream(targetFile, true);
        final PrintWriter printWriter =
            new PrintWriter(new OutputStreamWriter(fileOutputStream, configuration.getCharset()))) {
      logger.trace(
          "Writing {} to : {}", outputSourceType.toString(), targetFile.getCanonicalPath());

      try (BufferedReader br =
          new BufferedReader(
              new InputStreamReader(
                  new FileInputStream(configuration.getMasterLogFile()),
                  configuration.getCharset()))) {
        for (OutputRecord record;
            (record = gson.fromJson(br.readLine(), OutputRecord.class)) != null; ) {
          if (outputSourceType == OutputSourceType.ALL) {
            printWriter.println(record.getOutputText());
          } else if (outputSourceType == record.getOutputSourceType()) {
            printWriter.println(record.getOutputText());
          }
        }
      }
    }

    logger.info(
        "{} written completely to : {}",
        outputSourceType.toString(),
        targetFile.getCanonicalPath());
    return targetFile;
  }

  public static String joinString(String... stringArray) {
    return joinString(Arrays.asList(stringArray));
  }

  public static String joinString(List<String> stringList) {
    return stringList.stream().collect(Collectors.joining(Constants.EMPTY_STRING));
  }

  public static String generateThreadName(Configuration configuration, String suffix) {
    return joinString(configuration.getInterpreter(), configuration.getCommand(), suffix);
  }

  public static List<String> grepFile(final String regex, Configuration configuration)
      throws IOException {
    List<String> grepedLines = new ArrayList<>();
    logger.trace("Searching for regular expression : {}", regex);
    try (BufferedReader br =
        new BufferedReader(
            new InputStreamReader(
                new FileInputStream(configuration.getMasterLogFile()),
                configuration.getCharset()))) {
      for (OutputRecord record;
          (record = gson.fromJson(br.readLine(), OutputRecord.class)) != null; ) {
        if (record.getOutputText().matches(regex)) {
          grepedLines.add(
              Utilities.joinString(
                  record.getOutputSourceType().toString(), " >> ", record.getOutputText()));
          logger.trace("Found {} to match the regex : {}", record.getOutputSourceType(), regex);
        }
      }
    }
    return grepedLines;
  }
}
