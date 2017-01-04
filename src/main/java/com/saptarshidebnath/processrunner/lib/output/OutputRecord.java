package com.saptarshidebnath.processrunner.lib.output;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/** Stores each line of output from the Process */
public class OutputRecord {
  private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
  private final long timeStamp;
  private final OutputSourceType outputSourceType;
  private final String outputText;

  /**
   * Constructor to store the OutputRecord line by line
   *
   * @param outputSourceType {@link OutputSourceType}
   * @param outputText
   */
  public OutputRecord(final OutputSourceType outputSourceType, final String outputText) {
    this.timeStamp = getCurrentTime();
    this.outputSourceType = outputSourceType;
    this.outputText = outputText;
  }

  /**
   * Get the current timestamp in nano seconds
   *
   * @return
   */
  private static synchronized long getCurrentTime() {
    return System.nanoTime();
  }

  public long getTimeStamp() {
    return this.timeStamp;
  }

  public OutputSourceType getOutputSourceType() {
    return this.outputSourceType;
  }

  public String getOutputText() {
    return this.outputText;
  }

  /**
   * Get the current object in JSON format
   *
   * @return
   */
  public String getAsJson() {
    return gson.toJson(this);
  }
}
