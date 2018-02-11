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

package com.saptarshidebnath.lib.processrunner.process;

import com.saptarshidebnath.lib.processrunner.output.Output;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory method to to run command or to get an instance of {@link ProcessRunner}.
 *
 * <p>This is the only way to create a reference of {@link ProcessRunner}. Please note that if you
 * opt for Start process, then you either received an {@link Future} or an {@link Output}.
 */
public class ProcessRunnerFactory {

  private static final Logger logger = LoggerFactory.getLogger(ProcessRunnerFactory.class);

  /** Hidden constructor so that no body can create object of the class. */
  private ProcessRunnerFactory() {}

  /**
   * Create a instance of {@link ProcessRunner} by consuming a reference of the {@link
   * Configuration}. The process is then <strong>triggered in synchronously</strong>.
   *
   * @param configuration Takes a valid {@link Configuration} object.
   * @return a reference to {@link Output}
   * @throws IOException denoting there is an IO problem during writing the log files.
   * @throws InterruptedException there is a problem when writing the logs via thread enabled log
   *     handlers.
   * @throws ExecutionException this when the threads crash.
   */
  public static Output startProcess(final Configuration configuration)
      throws IOException, InterruptedException, ExecutionException {
    logger.debug("Starting process with config : {}", configuration);
    return new ProcessRunnerImpl(configuration).run();
  }

  /**
   * Create a instance of {@link ProcessRunner} by consuming a reference of the {@link
   * Configuration}. The process is then <strong>triggered asynchronously</strong>.
   *
   * @param configuration Takes a valid {@link Configuration} object.
   * @return a reference of {@link Future} of type {@link Output} from where you can retrieve the
   *     {@link Output} of the process.
   */
  public static Future<Output> startAsyncProcess(final Configuration configuration) {
    logger.debug("Starting asynchronous process with configuration : {}", configuration);
    return new ProcessRunnerImpl(configuration).runAsync();
  }

  /**
   * Create a instance of {@link ProcessRunner}. This method doesn't start the process and is upon
   * the developer to actually trigger the process.
   *
   * @param configuration a reference of {@link Configuration}. The {@link Configuration} can be
   *     build quite easily via {@link ConfigurationBuilder}
   * @return a reference of {@link ProcessRunner}
   */
  public static ProcessRunner getProcess(Configuration configuration) {
    logger.debug("Creating Process with the configuration : ", configuration);
    return new ProcessRunnerImpl(configuration);
  }
}
