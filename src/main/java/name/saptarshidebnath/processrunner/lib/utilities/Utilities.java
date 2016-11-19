package name.saptarshidebnath.processrunner.lib.utilities;

import java.io.File;
import java.io.IOException;

/** Created by saptarshi on 11/18/2016. */
public class Utilities {

  public static File createTempSysOut() throws IOException {
    return File.createTempFile(Constants.FILE_PREFIX_NAME_SYSOUT, Constants.FILE_SUFFIX_lOG);
  }

  public static File createTempSysErr() throws IOException {
    return File.createTempFile(Constants.FILE_PREFIX_NAME_SYSERR, Constants.FILE_SUFFIX_lOG);
  }
}
