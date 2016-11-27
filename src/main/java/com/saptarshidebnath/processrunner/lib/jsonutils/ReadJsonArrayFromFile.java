package com.saptarshidebnath.processrunner.lib.jsonutils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.saptarshidebnath.processrunner.lib.exception.JsonArrayReaderException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Read a Json Array form the file object by Object
 *
 * @param <T>
 */
public class ReadJsonArrayFromFile<T> {
  private final Gson gson;
  private boolean isFirstRead;
  private JsonReader jsonReader;

  {
    this.isFirstRead = true;
  }

  /**
   * Constructor to read a Json Array File Object by Object
   *
   * @param targetFile : {@link File} reference to the JSON file that needs to be read.
   * @throws IOException
   */
  public ReadJsonArrayFromFile(final File targetFile) throws IOException {
    this.jsonReader = new JsonReader(new FileReader(targetFile));
    this.gson = new GsonBuilder().setPrettyPrinting().create();
  }

  protected void finalize() throws IOException {
    this.cleanUp();
  }

  /**
   * Cleans up the reader stream
   *
   * @throws IOException
   */
  public synchronized void cleanUp() throws IOException {
    if (this.jsonReader != null) {
      this.jsonReader.close();
      this.jsonReader = null;
    }
  }

  public T readNext(final Class<T> clazz) throws IOException, JsonArrayReaderException {
    T object = null;
    if (this.jsonReader != null) {
      if (this.isFirstRead == true) {
        this.jsonReader.beginArray();
        this.isFirstRead = false;
      }

      synchronized (this) {
        if (this.jsonReader.hasNext()) {
          object = this.gson.fromJson(this.jsonReader, clazz);
        }
      }
    } else {
      throw new JsonArrayReaderException(
          "Json reader is already closed. Please create a new Object to read a new Json File");
    }
    return object;
  }
}
