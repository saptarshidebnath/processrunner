package name.saptarshidebnath.processrunner.lib.utilities;

import java.io.File;

/** Created by saptarshi on 11/18/2016. */
public interface Constants {
  String SPACE = " ";
  String FILE_PREFIX_NAME_SYSOUT = "ProcessRunner-sysout-";
  String FILE_PREFIX_NAME_SYSERR = "ProcessRunner-syserr-";
  String FILE_SUFFIX_lOG = ".log";
  File DEFAULT_CURRENT_DIR = new File(System.getProperty("user.dir"));
  File USER_HOME_DIR = new File(System.getProperty("user.home"));
}
