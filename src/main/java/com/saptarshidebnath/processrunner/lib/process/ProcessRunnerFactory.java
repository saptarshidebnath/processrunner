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
import com.saptarshidebnath.processrunner.lib.utilities.Constants;
import com.saptarshidebnath.processrunner.lib.utilities.Utilities;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.logging.Level;

import static com.saptarshidebnath.processrunner.lib.utilities.Constants.FILE_PREFIX_NAME_LOG_DUMP;

/** Factory method to to run command or to get an instance of {@link ProcessRunner} */
public class ProcessRunnerFactory {
  /** Hidden constructor so that no body can create object of the class. */
  private ProcessRunnerFactory() {}
  /**
   * Run a <strong>process synchronously</strong> and respond back with return a reference to {@link
   * Output}. Uses current directory as the working directory. Create temporary file for json
   * formatted master log. Master log File is auto deleted after execution.
   *
   * @param commandInterPreter : Command Interpreter to be used.
   * @param command : command to be executed.
   * @param logLevel {@link Level} value setting for the minimum {@link Level} for printing debug
   *     message.
   * @return a reference to {@link Output}
   * @throws ProcessException : Throws a {@link ProcessException} detailing what kind of error might
   *     have happened.
   */
  public static Output startProcess(
      final String commandInterPreter, final String command, final Level logLevel)
      throws ProcessException {
    try {
      return new ProcessRunnerImpl(
              new ProcessConfiguration(
                  commandInterPreter,
                  command,
                  Constants.DEFAULT_CURRENT_DIR,
                  File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON),
                  true,
                  logLevel))
          .run();
    } catch (final Exception e) {
      throw new ProcessException(e);
    }
  }

  /**
   * Creates a process with bare basic parameters. Supplies default {@link File} for json formatted
   * Master Log File. The Master log file is kept in the system and is upon the user to delete /
   * move / keep the same. To get the details of the Master Log File please use {@link
   * ProcessConfiguration#getMasterLogFile()}.
   *
   * @param commandInterPreter : The command interpreter.
   * @param command : The command to be executed.
   * @param workingDirectory : The working doriector as a {@link File} object.
   * @param logLevel {@link Level} value setting for the minimum {@link Level} for printing debug
   *     message.
   * @return a reference to the class {@link ProcessRunner} on which you can call {@link
   *     ProcessRunner#run()} to trigger the Process in synchronous manner. You can also all {@link
   *     ProcessRunner#run(boolean)} to run the Process asynchronously. Please see java doc for
   *     {@link ProcessRunner#run(boolean)} for more details.
   * @throws ProcessException : Throws a {@link ProcessException} detailing what kind of error might
   *     have happened.
   */
  public static ProcessRunner getProcess(
      final String commandInterPreter,
      final String command,
      final File workingDirectory,
      final Level logLevel)
      throws ProcessException {
    final File outputLogJsonFile;
    try {
      outputLogJsonFile = Utilities.createTempLogDump();
    } catch (final IOException e) {
      throw new ProcessException(e);
    }

    try {
      return new ProcessRunnerImpl(
          new ProcessConfiguration(
              commandInterPreter, command, workingDirectory, outputLogJsonFile, false, logLevel));
    } catch (final Exception e) {
      //
      // Delete file on exit.
      //
      outputLogJsonFile.deleteOnExit();
      throw new ProcessException(e);
    }
  }

  /**
   * Creates a ProcessRunner with all the required parameters.
   *
   * @param commandRunnerInterPreter : {@link String} value with the command interpreter to be used
   *     example "/bin/bash"
   * @param command : {@link String} value for the process we are going to run. Can be a shell
   *     script or a batch file also.
   * @param workingDirectory : {@link File} reference to the working directory from where it should
   *     be executing
   * @param logDump : {@link File} reference to the json log file where the logs will be stored.
   * @param autoDeleteFile {@link Boolean} object confiroming if the file need to be auto deleted at
   *     the end of the jvm exit
   * @param logLevel {@link Level} value setting for the minimum {@link Level} for printing debug
   *     message.
   * @return a reference to the class {@link ProcessRunner} On which you can call {@link
   *     ProcessRunner#run()} to trigger the Process in synchronous manner. You can also all {@link
   *     ProcessRunner#run(boolean)} to run the Process asynchronously. Please see java doc for
   *     {@link ProcessRunner#run(boolean)} for more details.
   * @throws ProcessException : Throws a {@link ProcessException} detailing what kind of error might
   *     have happened.
   */
  public static ProcessRunner getProcess(
      final String commandRunnerInterPreter,
      final String command,
      final File workingDirectory,
      final File logDump,
      final boolean autoDeleteFile,
      final Level logLevel)
      throws ProcessException {
    try {
      return new ProcessRunnerImpl(
          new ProcessConfiguration(
              commandRunnerInterPreter,
              command,
              workingDirectory,
              logDump,
              autoDeleteFile,
              logLevel));
    } catch (final Exception e) {
      throw new ProcessException(e);
    }
  }

  /**
   * Create a instance of {@link ProcessRunner} by consuming a reference of the {@link
   * ProcessConfiguration}. The process is then <strong>triggered in a synchronously</strong>.
   *
   * @param configuration Takes a valid {@link ProcessConfiguration} object.
   * @return a reference to {@link Output}
   * @throws ProcessException : Throws a {@link ProcessException} detailing what kind of error might
   *     have happened.
   */
  public static Output startProcess(final ProcessConfiguration configuration)
      throws ProcessException {
    try {
      return new ProcessRunnerImpl(configuration).run();
    } catch (final Exception ex) {
      throw new ProcessException(ex);
    }
  }

  /**
   * Create a instance of {@link ProcessRunner} by consuming a reference of the {@link
   * ProcessConfiguration}. The process is then <strong>triggered asynchronously</strong>.
   *
   * @param processsConfiguration Takes a valid {@link ProcessConfiguration} object
   * @param enableThreadedApproach Takes a flag {@link Boolean} variable doesn't matter if it is a
   *     true or false
   * @return a reference of {@link Future} of type {@link Output} from where you can retrieve the
   *     {@link Output} of the process.
   * @throws ProcessException : Throws a {@link ProcessException} detailing what kind of error might
   *     have happened.
   */
  public static Future<Output> startProcess(
      final ProcessConfiguration processsConfiguration, final boolean enableThreadedApproach)
      throws ProcessException {
    try {
      return new ProcessRunnerImpl(processsConfiguration).run(enableThreadedApproach);
    } catch (final Exception ex) {
      throw new ProcessException(ex);
    }
  }
}
