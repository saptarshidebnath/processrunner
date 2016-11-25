package com.saptarshidebnath.processrunner.lib;

import com.saptarshidebnath.processrunner.lib.utilities.Constants;

import java.io.File;
import java.io.IOException;

/** Process Runner interface is the base interface to run any system process or shell script */
public interface ProcessRunner extends Constants {

  /**
   * Searches for the regular expression throughout the content of the log
   *
   * @param regex : regular expression string to be searched throughout the content of the file.
   * @return
   * @throws IOException
   */
  boolean search(final String regex) throws IOException;

  /**
   * Triggers the process or command;
   *
   * @return the {@link Integer} exit code for the process
   */
  int run() throws IOException, InterruptedException;

  /**
   * Generate the sysout
   *
   * @param sysOut : {@link File} depicting where the sysout is going to stored
   * @return a {@link File}
   */
  File saveSysOut(File sysOut) throws IOException;

  /**
   * Generate the syserror
   *
   * @param sysError {@link File} depicting where the sys error is going to stored
   * @return
   */
  File saveSysError(File sysError) throws IOException;

  /**
   * Returns a {@link File} reference to the log where the Json Log Data is dumped
   *
   * @return
   */
  File getJsonLogDump();
}
