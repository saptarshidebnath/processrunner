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

import com.google.gson.GsonBuilder;
import com.saptarshidebnath.processrunner.lib.exception.JsonArrayReaderException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.logging.Logger;

import static com.saptarshidebnath.processrunner.lib.utilities.Constants.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

public class ReadJsonArrayFromFileTest {

  private final String jsonContent;
  private final String[] dataArrayToWrite;
  private final Logger logger;
  private ReadJsonArrayFromFile<String> testObject;
  private File testFile;

  public ReadJsonArrayFromFileTest() {
    this.dataArrayToWrite = new String[] {"1", "2", "3", "4", "5"};
    this.jsonContent = new GsonBuilder().create().toJson(this.dataArrayToWrite);
    this.logger = Logger.getLogger(this.getClass().getCanonicalName());
  }

  @Before
  public void setUp() throws Exception {
    this.testFile = File.createTempFile("test-read-object-prefix", "-suffix");
    final PrintWriter pw =
        new PrintWriter(
            new OutputStreamWriter(
                new FileOutputStream(this.testFile, true), Charset.defaultCharset()));
    pw.print(this.jsonContent);
    pw.flush();
    pw.close();
    this.testObject = new ReadJsonArrayFromFile<>(this.testFile, UTF_8);
  }

  @After
  public void tearDown() throws Exception {
    if (this.testFile.delete()) {
      this.logger.severe(
          "Unable to delete file : "
              + this.testFile.getCanonicalPath()
              + ". Please delete manually");
    }
    this.testObject.closeJsonReader();
  }

  @Test
  public void textReadNext() throws IOException, JsonArrayReaderException {
    final ArrayList<String> readData = new ArrayList<>();
    String data;
    do {
      data = this.testObject.readNext(String.class);
      if (data != null) {
        readData.add(data);
      }
    } while (data != null);
    assertThat(
        "Checking array length",
        readData.toArray(new String[readData.size()]),
        arrayWithSize(this.dataArrayToWrite.length));

    assertThat("Checking array contents", readData, hasItems(this.dataArrayToWrite));
  }

  @Test(expected = JsonArrayReaderException.class)
  public void writeJsonObjectAfterEndingJsonObject() throws IOException, JsonArrayReaderException {
    this.testObject.closeJsonReader();
    this.testObject.readNext(String.class);
  }
}
