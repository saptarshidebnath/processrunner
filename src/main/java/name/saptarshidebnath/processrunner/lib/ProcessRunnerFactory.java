package name.saptarshidebnath.processrunner.lib;

import name.saptarshidebnath.processrunner.lib.exception.ProcessConfigurationException;
import name.saptarshidebnath.processrunner.lib.utilities.Utilities;

import java.io.File;
import java.io.IOException;

import static name.saptarshidebnath.processrunner.lib.utilities.Constants.DEFAULT_CURRENT_DIR;

/** Factory method to to run command or to get an instance of {@link ProcessRunner} */
public class ProcessRunnerFactory {
  /**
   * Run a process and respond back with return code. Uses current directory as the working
   * directory. Create temporary file for sysout and syserror. Files are auto deleted after
   * execution.
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
                DEFAULT_CURRENT_DIR,
                Utilities.createTempSysOut(),
                Utilities.createTempSysErr(),
                true))
        .run();
  }

  /**
   * Creates a process with bare basic parameters. Supplies default file for sysout and syseror.
   * Auto deletion of file is set to false
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
            commandInterPreter,
            command,
            workingDirectory,
            Utilities.createTempSysOut(),
            Utilities.createTempSysErr(),
            false));
  }

  /**
   * Create a detailed process
   *
   * @param commandInterPreter : command interpreter to be used like /bin/bash
   * @param command : command to be executed
   * @param workingDirectory : {@link File} representing working directory for the command execution
   * @param sysOut : {@link File} where sysout to be stored
   * @param sysError : {@link File} where syserr to be stored
   * @param autodeleteFile : boolean to denote if files need to be deleted automatically
   * @return
   * @throws IOException
   * @throws ProcessConfigurationException
   */
  public static ProcessRunner getProcess(
      final String commandInterPreter,
      final String command,
      final File workingDirectory,
      final File sysOut,
      final File sysError,
      final boolean autodeleteFile)
      throws IOException, ProcessConfigurationException {
    return new ProcessRunnerImpl(
        new ProcessConfiguration(
            commandInterPreter, command, workingDirectory, sysOut, sysError, autodeleteFile));
  }
}
