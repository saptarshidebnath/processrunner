package name.saptarshidebnath.processrunner.lib;

import name.saptarshidebnath.processrunner.lib.utilities.Constants;

import java.io.File;
import java.io.IOException;

/** Process Runner interface is the base interface to run any system process or shell script */
public interface ProcessRunner extends Constants {

  boolean search(final String regex) throws IOException;

  /**
   * Triggers the command;
   *
   * @return the {@link Integer} exit code for the process
   */
  int run() throws IOException, InterruptedException;

  /**
   * Returns the {@link File} reference for sysout
   *
   * @return
   */
  File getSysOut();

  /**
   * Returns the {@link File} reference for the syserr
   *
   * @return
   */
  File getSysErr();
}
