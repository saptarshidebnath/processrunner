package com.saptarshidebnath.processrunner.lib.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saptarshidebnath.processrunner.lib.exception.JsonArrayWriterException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Write a json array to file one array element at a time. Class is type safed so that the array is
 * of single type only
 *
 * @param <T>
 */
public class WriteJsonArrayToFile<T> {

  private final Gson gson;
  private PrintWriter printWriter;
  private volatile Boolean firstElement = true;

  /**
   * Constructor with the target file in mind.
   *
   * @param targetFile : {@link File} reference where the content is going to be saved
   * @throws IOException
   */
  public WriteJsonArrayToFile(final File targetFile) throws IOException {
    this.printWriter = new PrintWriter(new FileWriter(targetFile, true));
    this.gson = new GsonBuilder().create();
  }

  /**
   * This method doesnt modify the {@link File} configured in teh constructor. It just marks that
   * the begining of a object by a flag. Actual start will be written when the {@link
   * WriteJsonArrayToFile#writeJsonObject(Object)} is called.
   */
  public void startJsonObject() {
    this.firstElement = true;
  }

  /**
   * Write a json object to the {@link File} configured. The library used {@link Gson} to convert
   * any object to json {@link String}.
   *
   * @param object : Object of Type T to be written to the disk
   * @throws IOException
   */
  public void writeJsonObject(final T object) throws IOException, JsonArrayWriterException {
    if (this.printWriter != null) {
      final String objectAsJson = this.gson.toJson(object);
      synchronized (this.printWriter) {
        final String stringToWrite = this.firstElement ? "[" + objectAsJson : ", " + objectAsJson;
        this.printWriter.println(stringToWrite);
      }
      this.firstElement = false;
    } else {
      throw new JsonArrayWriterException(
          "Json object is already written and file is closed. Please create a new proces ot write another json array");
    }
  }

  /**
   * Ends the json object and actually writes the same to the disk.
   *
   * @throws IOException
   */
  public synchronized void endJsonObjectWrite() throws IOException {
    this.printWriter.write("]");
    this.printWriter.flush();
  }

  /**
   * Cleans up the Streams so that the lock on the {@link File} is released.
   *
   * @throws IOException
   */
  public synchronized void cleanup() throws IOException {
    if (this.printWriter != null) {
      this.printWriter.close();
      this.printWriter = null;
    }
  }
}
