package com.saptarshidebnath.lib.processrunner.utilities.fileutils;

import com.saptarshidebnath.lib.processrunner.constants.ProcessRunnerConstants;
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
    return File.createTempFile(
        ProcessRunnerConstants.FILE_PREFIX_NAME_LOG_DUMP, ProcessRunnerConstants.FILE_SUFFIX_JSON);
  }
}
