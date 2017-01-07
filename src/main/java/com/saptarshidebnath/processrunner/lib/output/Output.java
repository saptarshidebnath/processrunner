package com.saptarshidebnath.processrunner.lib.output;

import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
import com.saptarshidebnath.processrunner.lib.process.ProcessRunner;

import java.io.File;

/**
 * The interface for the Object which is going to be returned after running {@link
 * ProcessRunner#run()} or {@link ProcessRunner#run(boolean)}.
 */
public interface Output {

  /**
   * Prints the {@link OutputRecord#getOutputText()} of type {@link OutputSourceType#SYSOUT} to the
   * {@link File} supplied.
   *
   * @param sysOut A {@link File} object where the log is going to be written.
   * @return A {@link File} object where the log has been written.
   * @throws ProcessException In case of any error. This is a generic error. To get teh details,
   *     please use {@link ProcessException#getCause()}.
   */
  File saveSysOut(final File sysOut) throws ProcessException;

  /**
   * Prints the {@link OutputRecord#getOutputText()} of type {@link OutputSourceType#SYSERROR} to
   * the {@link File} supplied.
   *
   * @param sysError A {@link File} object where the log is going to be written.
   * @return A {@link File} object where the log has been written.
   * @throws ProcessException In case of any error. This is a generic error. To get teh details,
   *     please use {@link ProcessException#getCause()}.
   */
  File saveSysError(final File sysError) throws ProcessException;

  /**
   * Returns the master log file originally captured while executing the Process. Its an Json Array
   * of type {@link OutputRecord}.
   *
   * @return a {@link File} reference.
   */
  File getMasterLog();

  /**
   * Returns the process exit / return code.
   *
   * @return return the exit code as an integer value from 0 - 255
   */
  int getReturnCode();

  /**
   *
   * @param regex
   * @return
   * @throws ProcessException
   */
  boolean searchMasterLog(final String regex) throws ProcessException;
}
