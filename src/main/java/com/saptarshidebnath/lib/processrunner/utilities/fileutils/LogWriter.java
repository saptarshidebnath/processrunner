package com.saptarshidebnath.lib.processrunner.utilities.fileutils;

import com.saptarshidebnath.lib.processrunner.configuration.Configuration;
import com.saptarshidebnath.lib.processrunner.constants.OutputSourceType;
import com.saptarshidebnath.lib.processrunner.constants.ProcessRunnerConstants;
import com.saptarshidebnath.lib.processrunner.model.OutputRecord;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogWriter {
  private static final Logger logger = LoggerFactory.getLogger(GrepFile.class);
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
  public File writeLog(
      final Configuration configuration,
      final File targetFile,
      final OutputSourceType outputSourceType)
      throws IOException {
    try (FileOutputStream fileOutputStream = new FileOutputStream(targetFile, Boolean.TRUE);
        PrintWriter printWriter =
            new PrintWriter(new OutputStreamWriter(fileOutputStream, configuration.getCharset()))) {
      logger.trace("Writing {} to : {}", outputSourceType, targetFile.getCanonicalPath());

      try (BufferedReader br =
          new BufferedReader(
              new InputStreamReader(
                  new FileInputStream(configuration.getMasterLogFile()),
                  configuration.getCharset()))) {
        for (OutputRecord record;
            (record = ProcessRunnerConstants.GSON.fromJson(br.readLine(), OutputRecord.class))
                != null; ) {
          if (outputSourceType == OutputSourceType.ALL
              || outputSourceType == record.getOutputSourceType()) {
            printWriter.println(record.getOutputText());
          }
        }
      }
    }
    logger.info("{} written completely to : {}", outputSourceType, targetFile.getCanonicalPath());
    return targetFile;
  }
}
