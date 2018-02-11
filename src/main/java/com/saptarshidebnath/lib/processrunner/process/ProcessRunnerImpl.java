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
import com.saptarshidebnath.lib.processrunner.output.OutputFactory;
import com.saptarshidebnath.lib.processrunner.output.ProcessLogHandler;
import com.saptarshidebnath.lib.processrunner.utilities.Constants;
import com.saptarshidebnath.lib.processrunner.utilities.Threadify;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link ProcessRunner} interface. Gives a solid body to the {@link
 * ProcessRunner}.
 */
class ProcessRunnerImpl implements ProcessRunner {
  private Logger logger = LoggerFactory.getLogger(ProcessRunner.class);
  private final Configuration configuration;
  private final Runtime runTime;
  private final String configurationAsString;

  /**
   * Constructor receiving the {@link Configuration} to create the process runner.
   *
   * @param configuration a valid object of {@link Configuration}
   */
  ProcessRunnerImpl(final Configuration configuration) {
    this.configurationAsString = configuration.toString();
    this.configuration = configuration;
    this.runTime = Runtime.getRuntime();
    logger.info("Process ProcessRunner created");
    logger.debug("With configuration : {}", this.configurationAsString);
  }

  /**
   * Runs the process with the provided configuration in the same {@link Thread}.
   *
   * @return integer value depicting the process exit code
   */
  @Override
  public Output run() throws IOException, InterruptedException, ExecutionException {
    final Output output;
    this.logger.info("Starting process execution");
    final StringBuilder commandToExecute = new StringBuilder();
    commandToExecute
        .append(this.configuration.getInterpreter())
        .append(Constants.SPACE_CHAR)
        .append(this.configuration.getCommand());
    logger.debug("Executing command : {}", commandToExecute);
    Path currentWorkingDir = this.configuration.getWorkingDir();
    File currentWorkingDirFile = null;
    if (null != currentWorkingDir) {
      currentWorkingDirFile = currentWorkingDir.toFile();
    }
    String[] environmentVariable =
        System.getenv()
            .entrySet()
            .stream()
            .map(setElement -> setElement.getKey() + "=" + setElement.getValue())
            .collect(Collectors.toList())
            .toArray(new String[] {});
    final Process currentProcess =
        this.runTime.exec(commandToExecute.toString(), environmentVariable, currentWorkingDirFile);
    ProcessLogHandler processLogHandler = new ProcessLogHandler(currentProcess, configuration);
    logger.trace("Waiting for Log handlers to complete writing / handling logs.");
    processLogHandler.waitForShutdown();
    logger.info("Waiting for the process to terminate");
    currentProcess.waitFor();
    final Integer processExitValue = currentProcess.exitValue();
    output = new OutputFactory().createOutput(this.configuration, processExitValue);
    logger.trace("Process exited with exit value : {}", processExitValue);
    return output;
  }

  /**
   * Runs the process with the provided configuration in the separate {@link Thread}.
   *
   * @return {@link Future} of type {@link Output} reference so that the result of the method
   *     invocation can be retrieved.
   */
  @Override
  public Future<Output> runAsync() {
    ExecutorService executorService = new Threadify().getProcessRunnerExecutorService();
    return executorService.submit(this::run);
  }

  @Override
  public String toString() {
    return  "ProcessRunnerImpl{" + "configuration=" + configuration
        + ", runTime=" + runTime
        + '}';
  }
}
