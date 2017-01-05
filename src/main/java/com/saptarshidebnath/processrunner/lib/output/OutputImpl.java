package com.saptarshidebnath.processrunner.lib.output;

import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
import com.saptarshidebnath.processrunner.lib.jsonutils.ReadJsonArrayFromFile;
import com.saptarshidebnath.processrunner.lib.utilities.Utilities;

import java.io.File;
import java.util.logging.Logger;

class OutputImpl implements Output {
  private final File masterLogFile;
  private final Logger logger;
  private final int returnCode;

  OutputImpl(final File jsonLogDump, final int returnCode) {
    this.masterLogFile = jsonLogDump;
    this.logger = Logger.getLogger(this.getClass().getCanonicalName());
    this.returnCode = returnCode;
  }

  @Override
  public File saveSysOut(final File sysOut) throws ProcessException {
    this.logger.info("Saving sys out to " + sysOut.getAbsolutePath());
    return Utilities.writeLog(sysOut, this.masterLogFile, OutputSourceType.SYSOUT);
  }

  @Override
  public File saveSysError(final File sysError) throws ProcessException {
    this.logger.info("Saving sys error to : " + sysError.getAbsolutePath());
    return Utilities.writeLog(sysError, this.masterLogFile, OutputSourceType.SYSERROR);
  }

  @Override
  public File getMasterLog() {
    return this.masterLogFile;
  }

  @Override
  public boolean searchMasterLog(final String regex) throws ProcessException {
    boolean isMatching = false;
    try {
      this.logger.info("Searching for regular expression :" + regex);
      final ReadJsonArrayFromFile<OutputRecord> readJsonArrayFromFile =
          new ReadJsonArrayFromFile<>(this.masterLogFile);
      OutputRecord outputRecord;
      do {
        outputRecord = readJsonArrayFromFile.readNext(OutputRecord.class);
        if (outputRecord != null) {
          isMatching = outputRecord.getOutputText().matches(regex);
        }
      } while (outputRecord != null && !isMatching);
      if (isMatching) {
        this.logger.info("Regex \'" + regex + "\" is found");
      } else {
        this.logger.info("Regex \'" + regex + "\" NOT found");
      }
      readJsonArrayFromFile.closeJsonReader();
    } catch (final Exception ex) {
      throw new ProcessException(ex);
    }
    return isMatching;
  }

  @Override
  public int getReturnCode() {
    return this.returnCode;
  }
}
