package com.saptarshidebnath.lib.processrunner.utilities.fileutils;

import com.saptarshidebnath.lib.processrunner.utilities.Constants;
import java.io.File;
import java.io.IOException;

public class TempFile {
  /**
   * Creates a temporary {@link File}.
   *
   * @return a reference to {@link File}
   * @throws IOException if there are any issues creating the {@link File}
   */
  public File createTempLogDump() throws IOException {
    return File.createTempFile(Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
  }
}
