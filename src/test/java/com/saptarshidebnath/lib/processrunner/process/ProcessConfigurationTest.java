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

import static com.saptarshidebnath.lib.processrunner.constants.ProcessRunnerConstants.FILE_PREFIX_NAME_LOG_DUMP;
import static java.lang.Boolean.FALSE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import com.saptarshidebnath.lib.processrunner.configuration.Configuration;
import com.saptarshidebnath.lib.processrunner.configuration.Configuration.ConfigBuilder;
import com.saptarshidebnath.lib.processrunner.constants.ProcessRunnerConstants;
import com.saptarshidebnath.lib.processrunner.exception.ProcessConfigurationException;
import com.saptarshidebnath.lib.processrunner.utilities.fileutils.TempFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;

/** Created by saptarshi on 1/1/2017. */
public class ProcessConfigurationTest {

  private static final String[] testObjectCreationWithCorrectValuecommandParamArray = {
    "a", "b", "c"
  };

  @Test
  public void testObjectCreationWithCorrectValue()
      throws IOException, ProcessConfigurationException {
    final String interpreter = "bash";
    final String command = "echo";
    final String commandParam = "Saptarshi";
    final File tempFile =
        File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, ProcessRunnerConstants.FILE_SUFFIX_JSON);
    final boolean fileSetToBeAutoDeleted = true;
    final Configuration configuration =
        new ConfigBuilder(interpreter, command)
            .setParam(commandParam)
            .setParamList(Arrays.asList(testObjectCreationWithCorrectValuecommandParamArray))
            .setWorkigDir(ProcessRunnerConstants.DEFAULT_CURRENT_DIR_PATH)
            .setMasterLogFile(tempFile, fileSetToBeAutoDeleted)
            .enableLogStreaming(false)
            .build();
    assertThat(
        "Validating process runner Interpreter : ",
        configuration.getInterpreter(),
        is(interpreter));
    assertThat(
        "Validating process runner command: ",
        configuration.getCommand(),
        is(
            command
                + " "
                + commandParam
                + " "
                + String.join(" ", testObjectCreationWithCorrectValuecommandParamArray)));
    assertThat(
        "Validating process configuration default work dir: ",
        configuration.getWorkingDir().toFile().getCanonicalPath(),
        is(ProcessRunnerConstants.DEFAULT_CURRENT_DIR.getCanonicalPath()));
    assertThat(
        "Validating process configuration json log dump: ",
        configuration.getMasterLogFile().getCanonicalPath(),
        is(tempFile.getCanonicalPath()));
    assertThat(
        "Validating process configuration json log file set for auto deletion : ",
        configuration.getAutoDeleteFileOnExit(),
        is(fileSetToBeAutoDeleted));
    assertThat("Validating logg streaming : ", configuration.isEnableLogStreaming(), is(FALSE));
  }

  @Test(expected = ProcessConfigurationException.class)
  public void testObjectCreationWithEmptyInterpreter()
      throws IOException, ProcessConfigurationException {
    final String interpreter = "";
    final String command = "echo Saptarshi";
    final File tempFile =
        File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, ProcessRunnerConstants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();
    final boolean fileSetToBeAutoDeleted = true;
    new ConfigBuilder(interpreter, command)
        .setWorkigDir(ProcessRunnerConstants.DEFAULT_CURRENT_DIR_PATH)
        .setMasterLogFile(tempFile, fileSetToBeAutoDeleted)
        .build();
  }

  @Test(expected = ProcessConfigurationException.class)
  public void testObjectCreationWithEmptyCommand()
      throws IOException, ProcessConfigurationException {
    final String interpreter = "bash";
    final String command = "";
    final File tempFile =
        File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, ProcessRunnerConstants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();
    final boolean fileSetToBeAutoDeleted = true;
    new ConfigBuilder(interpreter, command)
        .setWorkigDir(ProcessRunnerConstants.DEFAULT_CURRENT_DIR_PATH)
        .setMasterLogFile(tempFile, fileSetToBeAutoDeleted)
        .build();
  }

  @Test(expected = ProcessConfigurationException.class)
  public void testObjectCreationNonExistentCurrentDir()
      throws IOException, ProcessConfigurationException {
    final String interpreter = "bash";
    final String command = "echo saptarshi";
    final File tempFile =
        File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, ProcessRunnerConstants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();
    final boolean fileSetToBeAutoDeleted = true;
    new ConfigBuilder(interpreter, command)
        .setWorkigDir(new File("\\root\\").toPath())
        .setMasterLogFile(tempFile, fileSetToBeAutoDeleted)
        .build();
    //
    // Un reachable step.
    //
  }

  @Test(expected = ProcessConfigurationException.class)
  public void testObjectCreationCurrentDirSetAsFile()
      throws IOException, ProcessConfigurationException {
    final String interpreter = "bash";
    final String command = "echo saptarshi";
    final File tempFile =
        File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, ProcessRunnerConstants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();
    final boolean fileSetToBeAutoDeleted = true;
    new ConfigBuilder(interpreter, command)
        .setWorkigDir(tempFile.toPath())
        .setMasterLogFile(tempFile, fileSetToBeAutoDeleted)
        .build();
  }

  @Test(expected = ProcessConfigurationException.class)
  public void testNullParamToConfigurationBuilder() throws ProcessConfigurationException {
    new ConfigBuilder("bash", "echo hello!").setParam(null).build();
  }

  @Test(expected = ProcessConfigurationException.class)
  public void testEmptyParamToConfigurationBuilder() throws ProcessConfigurationException {
    new ConfigBuilder("bash", "echo hello!").setParam("").build();
  }

  @Test(expected = ProcessConfigurationException.class)
  public void testNullParamListToConfigurationBuilder() throws ProcessConfigurationException {
    new ConfigBuilder("bash", "echo hello!").setParamList(new ArrayList<>(0)).build();
  }

  @Test(expected = ProcessConfigurationException.class)
  public void testEmptyParamListToConfigurationBuilder() throws ProcessConfigurationException {
    new ConfigBuilder("bash", "echo hello!").setParamList(null).build();
  }

  @Test(expected = ProcessConfigurationException.class)
  public void testNullInterpreterToConfigurationBuilder() throws ProcessConfigurationException {
    new ConfigBuilder(null, "echo hello!").build();
  }

  @Test(expected = ProcessConfigurationException.class)
  public void testEmptyInterpreterToConfigurationBuilder() throws ProcessConfigurationException {
    new ConfigBuilder("", "echo hello!").build();
  }

  @Test(expected = ProcessConfigurationException.class)
  public void testNullCommandToConfigurationBuilder() throws ProcessConfigurationException {
    new ConfigBuilder("bash", null).build();
  }

  @Test(expected = ProcessConfigurationException.class)
  public void testEmptyCommandToConfigurationBuilder() throws ProcessConfigurationException {
    new ConfigBuilder("bash", "").build();
  }

  @Test
  public void testToStringMethod() throws ProcessConfigurationException, IOException {
    File masterLogFile = new TempFile().createTempLogDump();
    String configurationToString =
        new ConfigBuilder("bash", "echo")
            .setParam("Saptarshi")
            .setParamList(Arrays.asList("works", "on", "java"))
            .setWorkigDir(ProcessRunnerConstants.DEFAULT_CURRENT_DIR_PATH)
            .setMasterLogFile(masterLogFile, true, ProcessRunnerConstants.UTF_8)
            .enableLogStreaming(true)
            .build()
            .toString();
    assertThat(
        "Validating toString method => Interpreter",
        configurationToString,
        containsString("interpreter='bash'"));
    assertThat(
        "Validating toString method => Command",
        configurationToString,
        containsString("command='echo Saptarshi works on java'"));
    assertThat(
        "Validating toString method => WorkingDir",
        configurationToString,
        containsString("workingDir=" + ProcessRunnerConstants.DEFAULT_CURRENT_DIR_PATH.toString()));
    assertThat(
        "Validating toString method => MasterLogFile",
        configurationToString,
        containsString("masterLogFile=" + masterLogFile.getAbsolutePath()));
    assertThat(
        "Validating toString method => Charset",
        configurationToString,
        containsString("charset=" + ProcessRunnerConstants.UTF_8.name()));
    assertThat(
        "Validating toString method => EnableLogStreaming",
        configurationToString,
        containsString("enableLogStreaming=true"));
    assertThat(
        "Validating toString method => AutoDeleteFileOnExit",
        configurationToString,
        containsString("autoDeleteFileOnExit=true"));
  }
}
