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

package com.saptarshidebnath.lib.processrunner.jsonutils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.saptarshidebnath.lib.processrunner.utilities.fileutils.TempFile;
import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.Test;

/** Created by saptarshi on 12/7/2016. */
public class StringUtilsTest {
  private final File tempFile;

  public StringUtilsTest() throws IOException {
    this.tempFile = new TempFile().createTempLogDump();
  }

  @After
  public void tearDown() {
    final boolean response = this.tempFile.delete();
    assertThat("Checking if temporary file is deletable or not ", response, is(true));
  }

  @Test
  public void createTempLogDump() {
    final boolean response = this.tempFile.exists();
    assertThat("Checking if File exists ", response, is(true));
  }
}
