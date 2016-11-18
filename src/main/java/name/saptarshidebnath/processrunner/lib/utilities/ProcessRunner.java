package name.saptarshidebnath.processrunner.lib.utilities;

import java.io.File;

/** Process Runner interface is the base interface to run any system process or shell script */
public interface ProcessRunner {

  /**
   * Triggers the command;
   *
   * @return the {@link Integer} exit code for the process
   */
  int run();

  /**
   * Returns the {@link File} reference for sysout
   *
   * @return
   */
  File getSout();

  /**
   * Returns the {@link File} reference for the syserr
   *
   * @return
   */
  File getSysOut();

  /**
   * Returns the reference to a {@link File} for the output with the sysout and syserror merged like
   * a console output
   *
   * @return
   */
  File getOutPut();
}
