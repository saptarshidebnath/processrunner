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

package com.saptarshidebnath.lib.processrunner.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface Constants {
  int CACHE_SIZE = Integer.MAX_VALUE;
  int FILE_WRITER_OBJECT_SIZE = 128;
  String SPACE_STR = " ";
  char SPACE_CHAR = ' ';
  String FILE_PREFIX_NAME_LOG_DUMP = "ProcessRunner-log-dump-";
  String FILE_SUFFIX_JSON = ".json";
  String EMPTY_STRING = "";
  String NEW_LINE = System.lineSeparator();
  String GENERIC_ERROR = "Generic Error. Please see log for more details.";
  Charset UTF_8 = StandardCharsets.UTF_8;
  String userDir = System.getProperty("user.dir");
  File DEFAULT_CURRENT_DIR = new File(userDir);
  Path DEFAULT_CURRENT_DIR_PATH = Paths.get(userDir);
  String DISK_WRITER_THREAD_NAME_SUFFIX = "-dsk-rtr";
  String STREAM_READER_THREAD_NAME_SUFFIX = "-strm-rdr-";
  String PROCESS_RUNNER_THREAD_GROUP_NAME = "PR-";
  long THREAD_WAIT_TIME = 100L;
  Gson GSON = new GsonBuilder().create();
}
