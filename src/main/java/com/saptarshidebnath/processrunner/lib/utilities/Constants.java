package com.saptarshidebnath.processrunner.lib.utilities;

import java.io.File;

/** Created by saptarshi on 11/18/2016. */
public interface Constants {
  String SPACE = " ";
  String FILE_PREFIX_NAME_LOG_DUMP = "ProcessRunner-log-dump-";
  String FILE_SUFFIX_lOG = ".log";
  String FILE_SUFFIX_JSON = ".json";
  File DEFAULT_CURRENT_DIR = new File(System.getProperty("user.dir"));
  File USER_HOME_DIR = new File(System.getProperty("user.home"));
  String NEW_LINE = System.lineSeparator();
}
