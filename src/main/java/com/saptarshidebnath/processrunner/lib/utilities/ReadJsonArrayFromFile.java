package com.saptarshidebnath.processrunner.lib.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/** Created by saptarshi on 11/20/2016. */
public class ReadJsonArrayFromFile<T> {
  private final Gson gson;
  private JsonReader jsonReader;
  private Object semaphore;

  public ReadJsonArrayFromFile(final File targetFile) throws IOException {
    this.jsonReader = new JsonReader(new FileReader(targetFile));
    this.gson = new GsonBuilder().setPrettyPrinting().create();
  }

  protected void finalize() throws IOException {
    this.cleanUp();
  }

  public void cleanUp() throws IOException {
    if (this.jsonReader != null) {
      this.jsonReader.close();
      this.jsonReader = null;
    }
  }

  public T readNext(final Class<T> clazz) throws IOException {
    if (this.semaphore == null) {
      this.jsonReader.beginArray();
      this.semaphore = new Object();
    }
    T object = null;
    if (this.jsonReader.hasNext()) {
      object = this.gson.fromJson(this.jsonReader, clazz);
    }
    return object;
  }
}
