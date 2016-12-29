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
  private boolean isFirstRead;
  private JsonReader jsonReader;

  {
    this.isFirstRead = true;
  }

  /**
   * Constructor to read a Json Array File Object by Object
   *
   * @param targetFile : {@link File} reference to the JSON file that needs to be read.
   * @throws IOException to inform if there is any problem reading the {@link File} reference
   *     objects.
   */
  public ReadJsonArrayFromFile(final File targetFile) throws IOException {
    this.jsonReader =
        new JsonReader(
            new InputStreamReader(new FileInputStream(targetFile), Charset.defaultCharset()));
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
   * @throws IOException Throws IO exception
   * @throws JsonArrayReaderException Throws {@link JsonArrayReaderException} if the reader is
   *     already closed. Need to create a new {@link ReadJsonArrayFromFile} object to read the array
   *     from file.
   */
  public synchronized T readNext(final Class<T> clazz)
      throws IOException, JsonArrayReaderException {
    T object = null;
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
    return object;
  }
}
