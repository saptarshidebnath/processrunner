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

package com.saptarshidebnath.processrunner.lib.process;

import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
import com.saptarshidebnath.processrunner.lib.output.Output;

import java.util.concurrent.Future;

/** Process Runner interface is the base interface to run any system process or shell script */
public interface ProcessRunner {

  /**
   * Triggers the process or command.
   *
   * @return the {@link Integer} exit code for the process.
   * @throws ProcessException Throws {@link ProcessException} to denote that there is an error. You
   *     can get the cause by {@link ProcessException#getCause()}
   */
  Output run() throws ProcessException;

  /**
   * Runs the process as a {@link java.util.concurrent.Callable} {@link Thread} with default
   * priority. The method returns a {@link Future} reference from which the response of the method
   * can be retrieved.
   *
   * @param threadEnabled The {@link Boolean} input is a flag input. The value of the passed
   *     parameter doesnt matter. The process will run thread enabled even if you pass {@link
   *     Boolean#FALSE}.
   * @return A reference to the {@link Future} of type {@link Output} from which the user can
   *     retrieve the method output.
   */
  Future<Output> run(final boolean threadEnabled);
}
