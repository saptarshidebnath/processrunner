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
    //
    // Un reachable step.
    //
    configuration.getMasterLogFile();
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
