package com.saptarshidebnath.lib.processrunner.utilities.fileutils;

import com.saptarshidebnath.lib.processrunner.configuration.Configuration;
import com.saptarshidebnath.lib.processrunner.constants.OutputSourceType;
import com.saptarshidebnath.lib.processrunner.constants.ProcessRunnerConstants;
import com.saptarshidebnath.lib.processrunner.model.OutputRecord;
import com.saptarshidebnath.lib.processrunner.process.Runner;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrepFile {

  private static final Logger logger = LoggerFactory.getLogger(GrepFile.class);

  /**
   * Greps the masterlog file as defined in the {@link Configuration} for the regular expression.
   * The method looks into both i.e. {@link OutputSourceType#ALL}. Also the method presumes that the
   * {@link Runner} have executed successfully.
   *
   * @param regex A valid regular expression regular expression using which the file needs to be
   *     searched for.
   * @param configuration A configuration object.
   * @return a list of {@link OutputRecord}
   * @throws IOException on disk error.
   */
  public List<OutputRecord> grepFile(final String regex, Configuration configuration)
      throws IOException {
    List<OutputRecord> grepedLines = new ArrayList<>();
    logger.trace("Searching for regular expression : {}", regex);
    try (BufferedReader br =
        new BufferedReader(
            new InputStreamReader(
                new FileInputStream(configuration.getMasterLogFile()),
                configuration.getCharset()))) {
      for (OutputRecord record;
          (record = ProcessRunnerConstants.GSON.fromJson(br.readLine(), OutputRecord.class))
              != null; ) {
        if (record.getOutputText().matches(regex)) {
          grepedLines.add(record);
          logger.trace("Found {} to match the regex : {}", record.getOutputSourceType(), regex);
        }
      }
    }
    return grepedLines;
  }
}
