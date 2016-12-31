package com.saptarshidebnath.processrunner.lib.process;

import com.saptarshidebnath.processrunner.lib.exception.JsonArrayReaderException;
import com.saptarshidebnath.processrunner.lib.utilities.Constants;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Future;

/** Process Runner interface is the base interface to run any system process or shell script */
public interface ProcessRunner extends Constants {

  /**
   * Searches for the regular expression throughout the content of the log
   *
   * @param regex : regular expression string to be searched throughout the content of the file.
   * @return
   * @throws IOException
   */
  boolean search(final String regex) throws IOException, JsonArrayReaderException;

  /**
   * Triggers the process or command;
   *
   * @return the {@link Integer} exit code for the process
   */
  Integer run() throws IOException, InterruptedException;

  /**
   * Runs the process as a {@link java.util.concurrent.Callable} thread. The method returns a {@link
   * Future} reference from which the response of the method can be rtrived.
   *
   * @param threadEnabled The {@link Boolean} input is a flag input. The value of the passed
   *     parameter doesnt matter. The process will run thread enabled even if you pass {@link
   *     Boolean#FALSE}.
   * @return A reference to the {@link Future<Integer>} from which the user can retrieve the method
   *     output.
   */
  Future<Integer> run(final boolean threadEnabled);

  /**
   * Generate the sysout
   *
   * @param sysOut : {@link File} depicting where the sysout is going to stored
   * @return a {@link File}
   */
  File saveSysOut(File sysOut) throws IOException, JsonArrayReaderException;

  /**
   * Generate the syserror
   *
   * @param sysError {@link File} depicting where the sys error is going to stored
   * @return
   */
  File saveSysError(File sysError) throws IOException, JsonArrayReaderException;

  /**
   * Returns a {@link File} reference to the log where the Json Log Data is dumped
   *
   * @return
   */
  File getJsonLogDump();
}
