package com.saptarshidebnath.processrunner.lib.output;

import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
import com.saptarshidebnath.processrunner.lib.jsonutils.ReadJsonArrayFromFile;
import com.saptarshidebnath.processrunner.lib.process.ProcessConfiguration;
import com.saptarshidebnath.processrunner.lib.utilities.Utilities;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

class OutputImpl implements Output {
  private final ProcessConfiguration configuration;
  private final Logger logger;
  private final int returnCode;

  OutputImpl(final ProcessConfiguration configuration, final int returnCode) {
    this.configuration = configuration;
    this.logger = Logger.getLogger(this.getClass().getCanonicalName());
    this.logger.setLevel(this.configuration.getLogLevel());
    this.returnCode = returnCode;
  }

  @Override
  public File saveSysOut(final File sysOut) throws ProcessException {
    this.logger.log(Level.INFO, "Saving sys out to {0}", new Object[] {sysOut.getAbsolutePath()});
    return Utilities.writeLog(this.configuration, sysOut, OutputSourceType.SYSOUT);
  }

  @Override
  public File saveSysError(final File sysError) throws ProcessException {
    this.logger.log(
        Level.INFO, "Saving sys error to : {0}", new Object[] {sysError.getAbsolutePath()});
    return Utilities.writeLog(this.configuration, sysError, OutputSourceType.SYSERROR);
  }

  @Override
  public File getMasterLog() {
    return this.configuration.getMasterLogFile();
  }

  @Override
  public boolean searchMasterLog(final String regex) throws ProcessException {
    boolean isMatching = false;
    try {
      this.logger.log(Level.INFO, "Searching for regular expression : {0}", new Object[] {regex});
      final ReadJsonArrayFromFile<OutputRecord> readJsonArrayFromFile =
          new ReadJsonArrayFromFile<>(this.getMasterLog());
      OutputRecord outputRecord;
      do {
        outputRecord = readJsonArrayFromFile.readNext(OutputRecord.class);
        if (outputRecord != null) {
          isMatching = outputRecord.getOutputText().matches(regex);
        }
      } while (outputRecord != null && !isMatching);
      if (isMatching) {
        this.logger.log(Level.INFO, "Regex {0} is found", new Object[] {regex});
      } else {
        this.logger.log(Level.WARNING, "Regex {0} is NOT found", new Object[] {regex});
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
