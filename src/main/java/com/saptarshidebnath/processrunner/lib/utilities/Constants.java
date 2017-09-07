/*
 *
 * MIT License
 *
 * Copyright (c) [2016] [Saptarshi Debnath]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.saptarshidebnath.processrunner.lib.utilities;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressFBWarnings({"PATH_TRAVERSAL_IN"})
public class Constants {
  public static final String SPACE_STR = " ";
  public static final char SPACE_CHAR = ' ';
  public static final String FILE_PREFIX_NAME_LOG_DUMP = "ProcessRunner-log-dump-";
  public static final String FILE_SUFFIX_JSON = ".json";
  public static final String EMPTY_STRING = "";
  public static final String NEW_LINE = System.lineSeparator();
  public static final String GENERIC_ERROR = "Generic Error. Please see log for more details.";
  public static final ThreadGroup PROCESS_RUNNER_THREAD_GROUP =
      new ThreadGroup("PROCESS_RUNNER_0.0.2");
  public static final int LOGGER_THREAD_COUNT = 2;
  public static final Charset UTF_8 = Charset.forName("UTF-8");
  private static String userDir = System.getProperty("user.dir");

  public static final File DEFAULT_CURRENT_DIR = new File(userDir);

  public static final Path DEFAULT_CURRENT_DIR_PATH = Paths.get(userDir);

  private Constants() {}
}
