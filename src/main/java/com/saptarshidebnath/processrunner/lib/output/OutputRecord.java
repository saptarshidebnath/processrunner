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
   * Constructor to store the OutputRecord line by line.
   *
   * @param outputSourceType Type of {@link OutputSourceType} ie. either {@link
   *     OutputSourceType#SYSOUT} or {@link OutputSourceType#SYSERROR}
   * @param outputText The log as {@link String} that is logged.
   */
  public OutputRecord(final OutputSourceType outputSourceType, final String outputText) {
    this.timeStamp = getCurrentTime();
    this.outputSourceType = outputSourceType;
    this.outputText = outputText;
  }

  /**
   * Get the current timestamp in nano seconds.
   *
   * @return a {@link Long} number denoting {@link System#nanoTime()}
   */
  private static long getCurrentTime() {
    return System.nanoTime();
  }

  /**
   * Returns the registered timestamp.
   *
   * @return
   */
  public long getTimeStamp() {
    return this.timeStamp;
  }

  /**
   * Return current {@link OutputSourceType} for this {@link Output}.
   *
   * @return a reference to {@link OutputSourceType}
   */
  public OutputSourceType getOutputSourceType() {
    return this.outputSourceType;
  }

  /**
   * Returns the current log as {@link String}.
   *
   * @return an {@link String}
   */
  public String getOutputText() {
    return this.outputText;
  }

  /**
   * Get the current object in JSON format.
   *
   * @return as {@link String}
   */
  public String getAsJson() {
    return gson.toJson(this);
  }
}
