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
