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
import com.saptarshidebnath.processrunner.lib.exception.JsonArrayWriterException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Write a json array to file one array element at a time. Class is type safe so that the array is
 * of single object type only.
 *
 * @param <T> Type safe the writer for a particular JsonClass.
 */
@SuppressFBWarnings("IMC_IMMATURE_CLASS_NO_TOSTRING")
public class WriteJsonArrayToFile<T> {
  private final Gson gson;
  private PrintWriter printWriter;
  private volatile boolean firstElement = true;
  /**
   * Constructor with the target {@link File} where the array needs to be written.
   *
   * @param targetFile : {@link File} reference where the content is going to be saved.
   * @param charset : sets a reference of {@link Charset} which will be used for encoding while
   *     writing the master log file.
   * @throws IOException When the target file could not be opened for the {@link File}.
   */
  public WriteJsonArrayToFile(final File targetFile, Charset charset) throws IOException {
    this.printWriter =
        new PrintWriter(new OutputStreamWriter(new FileOutputStream(targetFile, true), charset));
    this.gson = new GsonBuilder().create();
  }

  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }

  /**
   * This method doesn't modify the {@link File} configured in the constructor. It just marks that
   * the beginning of a object by a flag. Actual start will be written when the {@link
   * WriteJsonArrayToFile#writeJsonObject(Object)} is called.
   */
  public void startJsonObject() {
    this.firstElement = true;
  }

  /**
   * Write a json object to the {@link File} configured. The library used {@link Gson} to convert
   * any object to json {@link String}.
   *
   * @param object : Object of Type T to be written to the disk.
   * @throws JsonArrayWriterException an {@link JsonArrayWriterException} if there is an error
   *     writing the object to the disk.
   */
  public synchronized void writeJsonObject(final T object) throws JsonArrayWriterException {
    if (this.printWriter != null) {
      final String objectAsJson = this.gson.toJson(object);
      final String stringToWrite = this.firstElement ? "[" + objectAsJson : ", " + objectAsJson;
      this.printWriter.println(stringToWrite);
      this.firstElement = false;
    } else {
      throw new JsonArrayWriterException(
          "Json object is already written and file is closed. Please create a new process ot write another json array");
    }
  }

  /** Ends the json object and actually writes the same to the disk. */
  public synchronized void endJsonObjectWrite() {
    this.printWriter.write("]");
    this.printWriter.flush();
  }

  /** Cleans up the Streams so that the lock on the {@link File} is released. */
  public synchronized void cleanup() {
    if (this.printWriter != null) {
      this.printWriter.close();
      this.printWriter = null;
    }
  }
}
