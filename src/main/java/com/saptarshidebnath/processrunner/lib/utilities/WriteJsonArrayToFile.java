package com.saptarshidebnath.processrunner.lib.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class WriteJsonArrayToFile<T> {

  private final Gson gson;
  private PrintWriter printWriter;
  private volatile Boolean firstElement = true;

  public WriteJsonArrayToFile(final File targetFile) throws IOException {
    this.printWriter = new PrintWriter(new FileWriter(targetFile, true));
    this.gson = new GsonBuilder().create();
  }

  public void startJsonObject() {
    this.firstElement = true;
  }

  public void writeJsonObject(final T object) throws IOException {
    final String objectAsJson = this.gson.toJson(object);
    synchronized (this.printWriter) {
      final String stringToWrite = this.firstElement ? "[" + objectAsJson : ", " + objectAsJson;
      this.printWriter.println(stringToWrite);
    }
    this.firstElement = false;
  }

  public void endJsonObjectWrite() throws IOException {
    this.printWriter.write("]");
    this.printWriter.flush();
  }

  public void cleanup() throws IOException {
    if (this.printWriter != null) {
      this.printWriter.close();
      this.printWriter = null;
    }
  }
}
