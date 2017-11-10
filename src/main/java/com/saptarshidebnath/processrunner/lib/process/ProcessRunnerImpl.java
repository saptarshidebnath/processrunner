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

import com.saptarshidebnath.processrunner.lib.exception.JsonArrayWriterException;
import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
import com.saptarshidebnath.processrunner.lib.jsonutils.ProcessLogHanndler;
import com.saptarshidebnath.processrunner.lib.jsonutils.WriteJsonArrayToFile;
import com.saptarshidebnath.processrunner.lib.output.Output;
import com.saptarshidebnath.processrunner.lib.output.OutputFactory;
import com.saptarshidebnath.processrunner.lib.output.OutputRecord;
import com.saptarshidebnath.processrunner.lib.output.OutputSourceType;
import com.saptarshidebnath.processrunner.lib.utilities.Constants;
import com.saptarshidebnath.processrunner.lib.utilities.Threadify;
import com.saptarshidebnath.processrunner.lib.utilities.Utilities;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Implementation of the {@link ProcessRunner} interface. Gives a solid body to the {@link
 * ProcessRunner}.
 */
class ProcessRunnerImpl implements ProcessRunner {
  private final Configuration configuration;
  private final Runtime runTime;
  private final WriteJsonArrayToFile<OutputRecord> jsonArrayToOutputStream;

  /**
   * Constructor receiving the {@link Configuration} to create the process runner.
   *
   * @param configuration a valid object of {@link Configuration}
   * @throws IOException if Unable to work with the log files
   */
  ProcessRunnerImpl(final Configuration configuration) throws IOException {
    this.configuration = configuration;
    this.runTime = Runtime.getRuntime();
    File masterLogFile = this.configuration.getMasterLogFile();
    if (masterLogFile != null) {
      this.jsonArrayToOutputStream =
          new WriteJsonArrayToFile<>(masterLogFile, this.configuration.getCharset());
    } else {
      this.jsonArrayToOutputStream = null;
    }
    logger.info("Process ProcessRunner created");
  }

  /**
   * Runs the process with the provided configuration in the same {@link Thread}.
   *
   * @return integer value depicting the process exit code
   * @throws ProcessException Throws {@link ProcessException} to denote that some error have
   *     occurred.
   */
  @Override
  public Output run() throws IOException, InterruptedException {
    final Output output;
    this.logger.info("Starting process");
    final StringBuilder commandToExecute = new StringBuilder();
    commandToExecute
        .append(this.configuration.getInterpreter())
        .append(Constants.SPACE_CHAR)
        .append(this.configuration.getCommand());
    logger.trace("Executing command : {}", commandToExecute.toString());
    final Process currentProcess =
        this.runTime.exec(
            commandToExecute.toString(),
            null,
            this.configuration.getWorkingDir() == null
                ? null
                : this.configuration.getWorkingDir().toFile());
    ProcessLogHanndler processLogHanndler = new ProcessLogHanndler(currentProcess, configuration);
    logger.info("Waiting for the process to terminate");
    currentProcess.waitFor();
    final Integer processExitValue = currentProcess.exitValue();
    output = OutputFactory.createOutput(this.configuration, processExitValue);
    logger.trace(
        Utilities.joinString("Process exited with exit value : ", processExitValue.toString()));
    logger.trace("Waiting for Log handlers to complete writing / handling logs.");
    processLogHanndler.waitForShutdown();
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
    ExecutorService executorService = Threadify.getProcessRunnerExecutorService();
    return executorService.submit(this::run);
  }

  /**
   * Log a inputStream to the log dump as configured in {@link Configuration}.
   *
   * @param inputStreamToWrite : {@link InputStream} from which the content is being read and
   *     written to a File
   * @param outputSourceType {@link OutputSourceType} depicting the source of the output
   */
  @SuppressFBWarnings("CRLF_INJECTION_LOGS")
  private void logData(
      final InputStream inputStreamToWrite, final OutputSourceType outputSourceType) {
    try {
      logger.trace(
          "Writing {} as jsonObject to {}",
          new Object[] {
            outputSourceType.toString(), this.configuration.getMasterLogFile().getCanonicalPath()
          });
      final Scanner scanner =
          new Scanner(inputStreamToWrite, configuration.getCharset().toString());
      String currentLine;
      while (scanner.hasNext()) {
        currentLine = scanner.nextLine();
        if (configuration.isEnableLogStreaming())
          logger.info(Utilities.joinString(outputSourceType.toString(), " >> ", currentLine));
        ProcessRunnerImpl.this.jsonArrayToOutputStream.writeJsonObject(
            new OutputRecord(outputSourceType, currentLine));
      }
    } catch (JsonArrayWriterException | IOException ex) {
      final StringWriter sw = new StringWriter();
      ex.printStackTrace(new PrintWriter(sw));
      logger.error(
          "Unable to log {}",
          new Object[] {this.configuration.getMasterLogFile().getAbsolutePath()});
      logger.error("Cause : {0}", ex);
      logger.error(sw.toString());
    }
  }

  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
