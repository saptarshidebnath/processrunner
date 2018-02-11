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

package com.saptarshidebnath.lib.processrunner.output;

import com.saptarshidebnath.lib.processrunner.process.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class to create reference for {@link Output}. It uses the {@link OutputImpl} which
 * implementes {@link Output}.
 */
public class OutputFactory {
  private static final Logger logger = LoggerFactory.getLogger(OutputFactory.class);

  /**
   * Creates a object of type {@link Output}.
   *
   * @param configuration Accepts a valid {@link Configuration} reference.
   * @param returnCode Accepts the exit code of process / script executed.
   * @return a reference of type {@link Output}
   */
  public Output createOutput(final Configuration configuration, final int returnCode) {
    logger.debug(
        "Creating Output for configuration {} with return code {}",
        configuration.toString(),
        String.valueOf(returnCode));
    return new OutputImpl(configuration, returnCode);
  }
}
