package com.saptarshidebnath.processrunner.lib.output;

import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
import com.saptarshidebnath.processrunner.lib.jsonutils.ReadJsonArrayFromFile;
import com.saptarshidebnath.processrunner.lib.process.ProcessConfiguration;
import com.saptarshidebnath.processrunner.lib.utilities.Utilities;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Default Implementation of {@link Output} */
class OutputImpl implements Output {
  private final ProcessConfiguration configuration;
  private final Logger logger;
  private final int returnCode;

  /**
   * Accepts {@link ProcessConfiguration} and retunr code to create a {@link Output} object.
   *
   * @param configuration a valid {@link ProcessConfiguration} object.
   * @param returnCode a {@link Integer} value typically ranging from 0 - 255
   */
  OutputImpl(final ProcessConfiguration configuration, final int returnCode) {
    this.configuration = configuration;
    this.logger = Logger.getLogger(this.getClass().getCanonicalName());
    this.logger.setLevel(this.configuration.getLogLevel());
    this.returnCode = returnCode;
  }

  /**
   * Prints the {@link OutputRecord#getOutputText()} of type {@link OutputSourceType#SYSOUT} to the
   * {@link File} supplied.
   *
   * @param sysOut A {@link File} object where the log is going to be written.
   * @return A {@link File} object where the log has been written.
   * @throws ProcessException In case of any error. This is a generic error. To get the details,
   *     please use {@link ProcessException#getCause()}.
   */
  @Override
  public File saveSysOut(final File sysOut) throws ProcessException {
    this.logger.log(Level.INFO, "Saving sys out to {0}", new Object[] {sysOut.getAbsolutePath()});
    return Utilities.writeLog(this.configuration, sysOut, OutputSourceType.SYSOUT);
  }

  /**
   * Prints the {@link OutputRecord#getOutputText()} of type {@link OutputSourceType#SYSERROR} to
   * the {@link File} supplied.
   *
   * @param sysError A {@link File} object where the log is going to be written.
   * @return A {@link File} object where the log has been written.
   * @throws ProcessException In case of any error. This is a generic error. To get the details,
   *     please use {@link ProcessException#getCause()}.
   */
  @Override
  public File saveSysError(final File sysError) throws ProcessException {
    this.logger.log(
        Level.INFO, "Saving sys error to : {0}", new Object[] {sysError.getAbsolutePath()});
    return Utilities.writeLog(this.configuration, sysError, OutputSourceType.SYSERROR);
  }

  /**
   * Returns the master log file originally captured while executing the Process. Its an Json Array
   * of type {@link OutputRecord}.
   *
   * @return a {@link File} reference to the json formatted master log .
   */
  @Override
  public File getMasterLog() {
    return this.configuration.getMasterLogFile();
  }

  /**
   * Search the content of the {@link ProcessConfiguration#getMasterLogFile()} for a particular
   * regex. The search is done line by line.
   *
   * @param regex a proper Regular Expression that need to be searched for.
   * @return a {@link Boolean#TRUE} or {@link Boolean#FALSE} depending upon if the search is
   *     positive or negative.
   * @throws ProcessException In case of any error. This is a generic error. To get the details,
   *     please use {@link ProcessException#getCause()}.
   */
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

  /**
   * Returns the process exit / return code.
   *
   * @return return the exit code as an integer value from 0 - 255
   */
  @Override
  public int getReturnCode() {
    return this.returnCode;
  }
}
