package com.saptarshidebnath.processrunner.lib.utilities;

import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
import com.saptarshidebnath.processrunner.lib.jsonutils.ReadJsonArrayFromFile;
import com.saptarshidebnath.processrunner.lib.output.OutputRecord;
import com.saptarshidebnath.processrunner.lib.output.OutputSourceType;
import com.saptarshidebnath.processrunner.lib.process.ProcessConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public class Utilities {

  /** Hidden Constructor. */
  private Utilities() {}

  public static File createTempLogDump() throws IOException {
    return File.createTempFile(Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
  }

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
      logger.info(
          String.format(
              "Writing %s to : %s", outputSourceType.toString(), targetFile.getCanonicalPath()));
      OutputRecord outputRecord;
      do {
        outputRecord = readJsonArrayFromFile.readNext(OutputRecord.class);
        final String currentOutputLine;
        if (outputRecord != null && outputRecord.getOutputSourceType() == outputSourceType) {
          currentOutputLine = outputRecord.getOutputText();
          logger.info(String.format("%s >> %s", outputSourceType.toString(), currentOutputLine));
          printWriter.println(currentOutputLine);
        }
      } while (outputRecord != null);
      logger.info(
          String.format(
              "%s written completely to : %s",
              outputSourceType.toString(), targetFile.getCanonicalPath()));
    } catch (final Exception e) {
      throw new ProcessException(e);
    }
    return targetFile;
  }
}
