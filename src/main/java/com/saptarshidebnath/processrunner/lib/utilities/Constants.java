package com.saptarshidebnath.processrunner.lib.utilities;

import java.io.File;

/** Created by saptarshi on 11/18/2016. */
public class Constants {
  public static final String SPACE = " ";
  public static final String FILE_PREFIX_NAME_LOG_DUMP = "ProcessRunner-log-dump-";
  public static final String FILE_SUFFIX_JSON = ".json";
  public static final File DEFAULT_CURRENT_DIR = new File(System.getProperty("user.dir"));
  public static final String NEW_LINE = System.lineSeparator();
  public static final String GENERIC_ERROR = "Genric Error. Please see log for more detials.";

  private Constants() {}
}
