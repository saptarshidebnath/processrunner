package com.saptarshidebnath.processrunner.lib.utilities;

import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
import com.saptarshidebnath.processrunner.lib.jsonutils.ReadJsonArrayFromFile;
import com.saptarshidebnath.processrunner.lib.output.OutputRecord;
import com.saptarshidebnath.processrunner.lib.output.OutputSourceType;

import java.io.*;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public class Utilities {
  private static final Logger logger = Logger.getLogger(Utilities.class.getCanonicalName());
  /** Hidden Constructor. */
  private Utilities() {}

  public static File createTempLogDump() throws IOException {
    return File.createTempFile(Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
  }

  public static File writeLog(
      final File targetFile, final File jsonLogDump, final OutputSourceType outputSourceType)
      throws ProcessException {

    try (final FileOutputStream fileOutputStream = new FileOutputStream(targetFile, true);
        final PrintWriter printWriter =
            new PrintWriter(new OutputStreamWriter(fileOutputStream, Charset.defaultCharset()))) {
      final ReadJsonArrayFromFile<OutputRecord> readJsonArrayFromFile =
          new ReadJsonArrayFromFile<>(jsonLogDump);
      logger.info(
          "Writing " + outputSourceType.toString() + " to : " + targetFile.getCanonicalPath());
      OutputRecord outputRecord;
      do {
        outputRecord = readJsonArrayFromFile.readNext(OutputRecord.class);
        final String currentOutputLine;
        if (outputRecord != null && outputRecord.getOutputSourceType() == outputSourceType) {
          currentOutputLine = outputRecord.getOutputText();
          logger.info(outputSourceType.toString() + " >> " + currentOutputLine);
          printWriter.println(currentOutputLine);
        }
      } while (outputRecord != null);
      logger.info(
          outputSourceType.toString()
              + " written completely to : "
              + targetFile.getCanonicalPath());
    } catch (final Exception e) {
      throw new ProcessException(e);
    }
    return targetFile;
  }
}
