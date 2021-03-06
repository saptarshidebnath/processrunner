/*
 * MIT License Copyright (c) [2016] [Saptarshi Debnath] Permission is hereby granted, free of
 * charge, to any person obtaining a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions: The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF
 * ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.saptarshidebnath.lib.processrunner.exception;

import com.saptarshidebnath.lib.processrunner.configuration.Configuration;

/**
 * Custom {@link Exception} denote that configuration is not set correctly.
 *
 * <p>Denotes that there might be a logical error while configuring the class {@link Configuration}
 *
 * @author Saptarshi Debnath (saptarshi.devnath@gmail.com)
 * @version 0.2.0
 * @since 0.2.0
 */
public class ProcessConfigurationException extends Exception {

  /**
   * The constructor accepting a string message.
   *
   * @param message The message that needs to be provided while creating the exception.
   */
  public ProcessConfigurationException(final String message) {
    super(message);
  }
}
