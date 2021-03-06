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

package com.saptarshidebnath.lib.processrunner.model;

import com.saptarshidebnath.lib.processrunner.constants.OutputSourceType;
import com.saptarshidebnath.lib.processrunner.constants.ProcessRunnerConstants;
import com.saptarshidebnath.lib.processrunner.output.Output;
import com.saptarshidebnath.lib.processrunner.process.Runner;

/**
 * Stores each line of output from the {@link Runner}. {@link OutputRecord} captures the follwoing
 * details : timeStamp, {@link OutputSourceType} and the actual output in String format.
 */
public class OutputRecord {

  private final long timeStamp;
  private final OutputSourceType outputSourceType;
  private final String outputText;

  /**
   * Constructor to store the OutputRecord line by line.
   *
   * @param outputSourceType Type of {@link OutputSourceType} ie. either {@link
   *     OutputSourceType#SYSOUT} or {@link OutputSourceType#SYSERR}
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
   * @return currently set {@link Long} timestamp for the current log.
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
    return ProcessRunnerConstants.GSON.toJson(this);
  }

  @Override
  public String toString() {
    return "OutputRecord{"
        + "timeStamp="
        + timeStamp
        + ", outputSourceType="
        + outputSourceType
        + ", outputText='"
        + outputText
        + '\''
        + '}';
  }
}
