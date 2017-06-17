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
import com.saptarshidebnath.processrunner.lib.exception.JsonArrayReaderException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Reads Object from a JSON array {@link File}.
 *
 * @param <T> Type safes the JsonArray File reader to a particular {@link Class}.
 */
public class ReadJsonArrayFromFile<T> {
  private final Gson gson;
  private boolean isFirstRead = true;
  private JsonReader jsonReader;

  /**
   * Constructor to read a Json Array File Object by Object
   *
   * @param targetFile : {@link File} reference to the JSON file that needs to be read.
   * @param charset : sets the decoding {@link Charset} of the file.
   * @throws IOException to inform if there is any problem reading the {@link File} reference
   *     objects.
   */
  public ReadJsonArrayFromFile(final File targetFile, Charset charset) throws IOException {
    this.jsonReader =
        new JsonReader(new InputStreamReader(new FileInputStream(targetFile), charset));
    this.gson = new GsonBuilder().setPrettyPrinting().create();
  }

  /**
   * Closes the reader stream and makes the object un usable.
   *
   * @throws IOException indicating some problem while closing the stream.
   */
  public synchronized void closeJsonReader() throws IOException {
    if (this.jsonReader != null) {
      this.jsonReader.close();
      this.jsonReader = null;
    }
  }

  /**
   * Reads the next object of type T from the file if it exists.
   *
   * @param clazz Takes the {@link Class} of the type for which the file needs to be read.
   * @return an object of type T
   * @throws JsonArrayReaderException Throws {@link JsonArrayReaderException} if the reader is
   *     already closed. Need to create a new {@link ReadJsonArrayFromFile} object to read the array
   *     from file.
   */
  public synchronized T readNext(final Class<T> clazz) throws JsonArrayReaderException {
    T object = null;
    try {

      if (this.jsonReader != null) {
        if (this.isFirstRead) {
          this.jsonReader.beginArray();
          this.isFirstRead = false;
        }
        if (this.jsonReader.hasNext()) {
          object = this.gson.fromJson(this.jsonReader, clazz);
        }
      } else {
        throw new JsonArrayReaderException(
            "Json reader is already closed. Please create a new Object to read a new Json File");
      }
    } catch (final Exception ex) {
      throw new JsonArrayReaderException(ex);
    }
    return object;
  }
}
