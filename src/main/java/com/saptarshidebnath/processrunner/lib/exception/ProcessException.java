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

import com.saptarshidebnath.processrunner.lib.utilities.Constants;

/**
 * Blanket custom {@link Exception} to be used by the {@link
 * com.saptarshidebnath.processrunner.lib.process.ProcessRunnerFactory} class and all public facing
 * methods for this ProcessRunner library. The Developer should be able to get the cause from the
 * {@link ProcessException#getCause()} detailing exactly what triggered the exception.
 */
public class ProcessException extends Exception {
  /**
   * Creates a {@link ProcessException} from another {@link Exception}
   *
   * @param ex Accepts a {@link Exception} object as input.
   */
  public ProcessException(final Exception ex) {
    super(ex);
  }

  /**
   * Creates a generic {@link ProcessException} with the message {@link Constants#GENERIC_ERROR}.
   */
  public ProcessException() {
    this(new Exception(Constants.GENERIC_ERROR));
  }
}
