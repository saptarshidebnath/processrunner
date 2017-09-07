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

package com.saptarshidebnath.processrunner.lib.jsonutils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.saptarshidebnath.processrunner.lib.exception.JsonArrayWriterException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;

import static com.saptarshidebnath.processrunner.lib.utilities.Constants.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

/** Created by saptarshi on 11/25/2016. */
public class WriteJsonArrayToFileTest {
  private final File tempFile;
  private final WriteJsonArrayToFile<String> testObject;
  private final Gson gson;
  private final Logger logger;
  private String[] arrayOfDataToWrite;

  @SuppressFBWarnings("PCAIL_POSSIBLE_CONSTANT_ALLOCATION_IN_LOOP")
  public WriteJsonArrayToFileTest() throws IOException {

    this.tempFile = File.createTempFile("test-prefix", "test-suffix");
    final ArrayList<String> dataList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      dataList.add(new Date().toString());
    }
    this.arrayOfDataToWrite = new String[dataList.size()];
    this.arrayOfDataToWrite = dataList.toArray(this.arrayOfDataToWrite);
    this.testObject = new WriteJsonArrayToFile<>(this.tempFile, UTF_8);
    this.gson = new GsonBuilder().create();
    this.logger = Logger.getLogger(this.getClass().getCanonicalName());
  }

  @After
  public void tearDown() throws IOException {
    if (!this.tempFile.delete()) {
      this.logger.severe(
          "Unable to delete file : "
              + this.tempFile.getCanonicalPath()
              + ". Please delete the same manually.");
    }
  }

  @Test
  public void writeJsonObject() throws Exception {
    this.testObject.startJsonObject();
    for (final String currentLine : this.arrayOfDataToWrite) {
      this.testObject.writeJsonObject(currentLine);
    }
    this.testObject.endJsonObjectWrite();
    this.testObject.cleanup();
    final JsonReader reader =
        new JsonReader(
            new InputStreamReader(new FileInputStream(this.tempFile), Charset.defaultCharset()));
    final String[] arrayOfDataReadFromTestFile = this.gson.fromJson(reader, String[].class);

    assertThat(
        "Checking array length",
        arrayOfDataReadFromTestFile,
        arrayWithSize(this.arrayOfDataToWrite.length));

    assertThat(
        "Checking array contents",
        Arrays.asList(arrayOfDataReadFromTestFile),
        hasItems(this.arrayOfDataToWrite));
    //
    // This should'nt throw exception if clean up is already called
    //
    this.testObject.cleanup();
  }

  @Test(expected = JsonArrayWriterException.class)
  public void writeJsonObjectAfterEndingJsonObject() throws IOException, JsonArrayWriterException {
    this.testObject.startJsonObject();
    this.testObject.endJsonObjectWrite();
    this.testObject.cleanup();
    this.testObject.writeJsonObject("Test String");
  }
}
