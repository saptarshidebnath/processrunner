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

package com.saptarshidebnath.processrunner.lib.exception;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Custom {@link Exception} denote that the {@link
 * com.saptarshidebnath.processrunner.lib.jsonutils.ReadJsonArrayFromFile } is not configured
 * correctly.
 */
public class JsonArrayReaderException extends Exception {

  /**
   * The constructor accepting a string message.
   *
   * @param message Takes a String as input
   */
  public JsonArrayReaderException(final String message) {
    super(message);
  }

  /**
   * {@link JsonArrayReaderException} wrapper for already existing exception.
   *
   * @param exception Consumes a object of {@link Exception}
   */
  @SuppressFBWarnings("OPM_OVERLY_PERMISSIVE_METHOD")
  public JsonArrayReaderException(final Exception exception) {
    this.initCause(exception);
  }
}
