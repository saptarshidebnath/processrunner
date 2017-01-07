package com.saptarshidebnath.processrunner.lib.process;

import com.saptarshidebnath.processrunner.lib.exception.ProcessConfigurationException;
import com.saptarshidebnath.processrunner.lib.utilities.Constants;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import static com.saptarshidebnath.processrunner.lib.utilities.Constants.FILE_PREFIX_NAME_LOG_DUMP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/** Created by saptarshi on 1/1/2017. */
public class ProcessConfigurationTest {
  @Test
  public void testObjectCreationWithCorrectValue()
      throws IOException, ProcessConfigurationException {
    final String interpreter = "bash";
    final String command = "echo Saptarshi";
    final File tempFile =
        File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
    final boolean fileSetToBeAutoDeleted = true;
    final Level logLevel = Level.OFF;
    final ProcessConfiguration configuration =
        new ProcessConfiguration(
            interpreter,
            command,
            Constants.DEFAULT_CURRENT_DIR,
            tempFile,
            fileSetToBeAutoDeleted,
            logLevel);
    assertThat(
        "Validating process runner Interpreter : ",
        configuration.getCommandRunnerInterPreter(),
        is(interpreter));
    assertThat("Validating process runner command: ", configuration.getCommand(), is(command));
    assertThat(
        "Validating process configuration default work dir: ",
        configuration.getCurrentDirectory().getCanonicalPath(),
        is(Constants.DEFAULT_CURRENT_DIR.getCanonicalPath()));
    assertThat(
        "Validating process configuration json log dump: ",
        configuration.getMasterLogFile().getCanonicalPath(),
        is(tempFile.getCanonicalPath()));
    assertThat(
        "Validating process configuration json log file set for auto deletion : ",
        configuration.getAutoDeleteFileOnExit(),
        is(fileSetToBeAutoDeleted));
    assertThat(
        "Validating process configuration debug value : ",
        configuration.getLogLevel(),
        is(logLevel));
  }

  @Test(expected = ProcessConfigurationException.class)
  public void testObjectCreationWithEmptyInterpreter()
      throws IOException, ProcessConfigurationException {
    final String interpreter = "";
    final String command = "echo Saptarshi";
    final File tempFile =
        File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();
    final Level logLevel = Level.ALL;
    final boolean fileSetToBeAutoDeleted = true;
    new ProcessConfiguration(
        interpreter,
        command,
        Constants.DEFAULT_CURRENT_DIR,
        tempFile,
        fileSetToBeAutoDeleted,
        logLevel);
  }

  @Test(expected = ProcessConfigurationException.class)
  public void testObjectCreationWithEmptyCommand()
      throws IOException, ProcessConfigurationException {
    final String interpreter = "bash";
    final String command = "";
    final File tempFile =
        File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();
    final Level logLevel = Level.FINER;
    final boolean fileSetToBeAutoDeleted = true;
    new ProcessConfiguration(
        interpreter,
        command,
        Constants.DEFAULT_CURRENT_DIR,
        tempFile,
        fileSetToBeAutoDeleted,
        logLevel);
  }

  @Test(expected = ProcessConfigurationException.class)
  public void testObjectCreationNonExistentCurrentDir()
      throws IOException, ProcessConfigurationException {
    final String interpreter = "bash";
    final String command = "echo saptarshi";
    final File tempFile =
        File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();
    final Level logLevel = Level.FINER;
    final boolean fileSetToBeAutoDeleted = true;
    final ProcessConfiguration configuration =
        new ProcessConfiguration(
            interpreter, command, new File("a:\\"), tempFile, fileSetToBeAutoDeleted, logLevel);
  }

  @Test(expected = ProcessConfigurationException.class)
  public void testObjectCreationCurrentDirSetAsFile()
      throws IOException, ProcessConfigurationException {
    final String interpreter = "bash";
    final String command = "echo saptarshi";
    final File tempFile =
        File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();
    final boolean fileSetToBeAutoDeleted = true;
    final Level logLevel = Level.FINEST;
    new ProcessConfiguration(
        interpreter, command, tempFile, tempFile, fileSetToBeAutoDeleted, logLevel);
  }
}
