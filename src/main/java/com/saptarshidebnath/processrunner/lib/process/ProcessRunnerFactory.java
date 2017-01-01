package com.saptarshidebnath.processrunner.lib.process;

import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
import com.saptarshidebnath.processrunner.lib.utilities.Constants;
import com.saptarshidebnath.processrunner.lib.utilities.Utilities;

import java.io.File;

import static com.saptarshidebnath.processrunner.lib.utilities.Constants.FILE_PREFIX_NAME_LOG_DUMP;

/** Factory method to to run command or to get an instance of {@link ProcessRunner} */
public class ProcessRunnerFactory {
  /** Hidden constructor so that no body can create object of the same. */
  private ProcessRunnerFactory() {}
  /**
   * Run a process synchronously and respond back with return code. Uses current directory as the
   * working directory. Create temporary file for json log dump. Files are auto deleted after
   * execution.
   *
   * @param commandInterPreter : Command Interpreter to be used
   * @param command : command to be executed
   * @return the return code of the process. An {@link Integer} ranging from <strong>0 -
   *     255</strong>
   * @throws ProcessException : Throws a {@link ProcessException} detailing what kind of error might
   *     have happened.
   */
  public static int startProcess(final String commandInterPreter, final String command)
      throws ProcessException {
    try {
      return new ProcessRunnerImpl(
              new ProcessConfiguration(
                  commandInterPreter,
                  command,
                  Constants.DEFAULT_CURRENT_DIR,
                  File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON),
                  true))
          .run();
    } catch (final Exception e) {
      throw new ProcessException(e);
    }
  }

  /**
   * Creates a process with bare basic parameters. Supplies default {@link File} for log dump. Auto
   * deletion of file is set to false.
   *
   * @param commandInterPreter : The command interpreter
   * @param command : The command to be executed
   * @param workingDirectory : The working doriector as a {@link File} object
   * @return a reference to the class {@link ProcessRunner} On which you can call {@link
   *     ProcessRunner#run()} to trigger the Process in synchronous manner. You can also all {@link
   *     ProcessRunner#run(boolean)} to run the Process asynchronously. Please see java doc for
   *     {@link ProcessRunner#run(boolean)} for more details.
   * @throws ProcessException : Throws a {@link ProcessException} detailing what kind of error might
   *     have happened.
   */
  public static ProcessRunner getProcess(
      final String commandInterPreter, final String command, final File workingDirectory)
      throws ProcessException {
    try {
      return new ProcessRunnerImpl(
          new ProcessConfiguration(
              commandInterPreter, command, workingDirectory, Utilities.createTempLogDump(), false));
    } catch (final Exception e) {
      throw new ProcessException(e);
    }
  }

  /**
   * Creates a ProcessRunner with all the required parameters
   *
   * @param commandRunnerInterPreter : {@link String} value with the command interpreter to be used
   *     example "/bin/bash"
   * @param command : {@link String} value for the process we are going to run. Can be a shell
   *     script or a batch file also
   * @param workingDirectory : {@link File} reference to the working directory from where it should
   *     be executing
   * @param logDump : {@link File} reference to the json log file where the logs will be stored.
   * @param autoDeleteFile {@link Boolean} object confiroming if the file need to be auto deleted at
   *     the end of the jvm exit
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
      final boolean autoDeleteFile)
      throws ProcessException {
    try {
      return new ProcessRunnerImpl(
          new ProcessConfiguration(
              commandRunnerInterPreter, command, workingDirectory, logDump, autoDeleteFile));
    } catch (final Exception e) {
      throw new ProcessException(e);
    }
  }
}
