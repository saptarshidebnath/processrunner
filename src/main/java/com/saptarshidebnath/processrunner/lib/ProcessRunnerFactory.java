package com.saptarshidebnath.processrunner.lib;

import com.saptarshidebnath.processrunner.lib.exception.ProcessConfigurationException;
import com.saptarshidebnath.processrunner.lib.utilities.Constants;
import com.saptarshidebnath.processrunner.lib.utilities.Utilities;

import java.io.File;
import java.io.IOException;

import static com.saptarshidebnath.processrunner.lib.utilities.Constants.FILE_PREFIX_NAME_LOG_DUMP;

/** Factory method to to run command or to get an instance of {@link ProcessRunner} */
public class ProcessRunnerFactory {
  /**
   * Run a process and respond back with return code. Uses current directory as the working
   * directory. Create temporary file for json log dump. Files are auto deleted after execution.
   *
   * @param commandInterPreter : Command Interpreter to be used
   * @param command : command to be executed
   * @return
   * @throws IOException
   * @throws ProcessConfigurationException
   * @throws InterruptedException
   */
  public static int startProcess(final String commandInterPreter, final String command)
      throws IOException, ProcessConfigurationException, InterruptedException {
    return new ProcessRunnerImpl(
            new ProcessConfiguration(
                commandInterPreter,
                command,
                Constants.DEFAULT_CURRENT_DIR,
                File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON),
                true))
        .run();
  }

  /**
   * Creates a process with bare basic parameters. Supplies default {@link File} for log dump. Auto
   * deletion of file is set to false.
   *
   * @param commandInterPreter : The command interpreter
   * @param command : The command to be executed
   * @param workingDirectory : The working doriector as a {@link File} object
   * @return
   * @throws IOException
   * @throws ProcessConfigurationException
   */
  public static ProcessRunner getProcess(
      final String commandInterPreter, final String command, final File workingDirectory)
      throws IOException, ProcessConfigurationException {
    return new ProcessRunnerImpl(
        new ProcessConfiguration(
            commandInterPreter, command, workingDirectory, Utilities.createTempLogDump(), false));
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
   * @return
   * @throws IOException
   * @throws ProcessConfigurationException
   */
  public static ProcessRunner getProcess(
      final String commandRunnerInterPreter,
      final String command,
      final File workingDirectory,
      final File logDump,
      final boolean autoDeleteFile)
      throws IOException, ProcessConfigurationException {
    return new ProcessRunnerImpl(
        new ProcessConfiguration(
            commandRunnerInterPreter, command, workingDirectory, logDump, autoDeleteFile));
  }
}
