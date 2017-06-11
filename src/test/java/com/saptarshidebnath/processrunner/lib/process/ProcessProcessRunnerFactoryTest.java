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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.saptarshidebnath.processrunner.lib.exception.ProcessConfigurationException;
import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
import com.saptarshidebnath.processrunner.lib.output.Output;
import com.saptarshidebnath.processrunner.lib.output.OutputRecord;
import com.saptarshidebnath.processrunner.lib.utilities.Constants;
import com.saptarshidebnath.processrunner.lib.utilities.Utilities;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.StringStartsWith.startsWith;

/** Created by saptarshi on 12/22/2016. */
public class ProcessProcessRunnerFactoryTest {

  private final Gson gson;
  private final int arryPosition;

  public ProcessProcessRunnerFactoryTest() {
    this.gson = new GsonBuilder().setPrettyPrinting().create();
    if (SystemUtils.IS_OS_WINDOWS) {
      this.arryPosition = 1;
    } else {
      this.arryPosition = 0;
    }
  }

  @Test
  public void startProcess()
      throws ProcessConfigurationException, IOException, InterruptedException, ProcessException {
    final Output response =
        ProcessRunnerFactory.startProcess(
            new ConfigurationBuilder(getDefaultInterpreter(), getInterPreterVersion())
                .setLogLevel(Level.ALL)
                .build());
    assertThat("Validating process runner for simple process : ", response.getReturnCode(), is(0));
  }

  @Test(expected = ProcessConfigurationException.class)
  public void startProcessWithWrongParmeters()
      throws ProcessConfigurationException, IOException, InterruptedException, ProcessException {
    ProcessRunnerFactory.startProcess(
        new ConfigurationBuilder("", getInterPreterVersion()).setLogLevel(Level.OFF).build());
  }

  @Test(expected = ProcessConfigurationException.class)
  public void getProcessWithLessParamsWrongParamets()
      throws ProcessException, ProcessConfigurationException, IOException {
    Configuration configuration = new ConfigurationBuilder(getDefaultInterpreter(), "").build();
  }

  @Test(expected = ProcessConfigurationException.class)
  public void getProcessMoreParamWrongParamets()
      throws IOException, ProcessConfigurationException, InterruptedException, ProcessException {
    final File tempFile =
        File.createTempFile(Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();

    ProcessRunnerFactory.startProcess(
        new ConfigurationBuilder("", getInterPreterVersion())
            .setLogLevel(Level.ALL)
            .setWorkigDir(Constants.DEFAULT_CURRENT_DIR_PATH)
            .setMasterLogFile(tempFile, false)
            .build());
  }

  @Test
  public void startProcessWithProcessConfig()
      throws IOException, ProcessConfigurationException, InterruptedException, ProcessException {
    final File tempFile =
        File.createTempFile(Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();

    final Output response =
        ProcessRunnerFactory.startProcess(
            new ConfigurationBuilder(getDefaultInterpreter(), getInterPreterVersion())
                .setWorkigDir(Constants.DEFAULT_CURRENT_DIR_PATH)
                .setMasterLogFile(tempFile, true)
                .setLogLevel(Level.ALL)
                .build());
    assertThat("Validating process return code : ", response.getReturnCode(), is(0));
  }

  @Test
  public void searchContent()
      throws ProcessException, IOException, ProcessConfigurationException, ExecutionException,
          InterruptedException {

    final Output response =
        ProcessRunnerFactory.startProcess(
            new ConfigurationBuilder(getDefaultInterpreter(), getInterPreterVersion())
                .setWorkigDir(Constants.DEFAULT_CURRENT_DIR_PATH)
                .setMasterLogFile(Utilities.createTempLogDump(), true)
                .setLogLevel(Level.SEVERE)
                .build());
    assertThat("Validating process return code : ", response.getReturnCode(), is(0));

    if (SystemUtils.IS_OS_LINUX) {
      assertThat(
          "Validating searchMasterLog result for content in the output in UNIX : ",
          response.searchMasterLog(".*GNU.*"),
          is(true));
    } else if (SystemUtils.IS_OS_WINDOWS) {
      assertThat(
          "Validating searchMasterLog result for content in the output in Windows : ",
          response.searchMasterLog("Microsoft Windows.*"),
          is(true));
    }
  }

  @Test
  public void searchContentNegativeTestCase()
      throws ProcessException, IOException, ProcessConfigurationException, ExecutionException,
          InterruptedException {

    final ProcessRunner processRunner =
        ProcessRunnerFactory.getProcess(
            new ConfigurationBuilder(getDefaultInterpreter(), getInterPreterVersion())
                .setWorkigDir(Constants.DEFAULT_CURRENT_DIR_PATH)
                .setMasterLogFile(Utilities.createTempLogDump(), true)
                .setLogLevel(Level.SEVERE)
                .build());

    final Output response = processRunner.run();
    assertThat("Validating process return code : ", response.getReturnCode(), is(0));

    assertThat(
        "Validating searchMasterLog result for content in the output in UNIX : ",
        response.searchMasterLog("Saptarshi"),
        is(false));
  }

  @Test
  public void startThreadedProcessWithProcessConfig()
      throws ProcessException, IOException, ProcessConfigurationException, ExecutionException,
          InterruptedException {
    final File tempFile =
        File.createTempFile(Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();

    final Future<Output> response =
        ProcessRunnerFactory.startAsyncProcess(
            new ConfigurationBuilder(getDefaultInterpreter(), getInterPreterVersion())
                .setWorkigDir(Constants.DEFAULT_CURRENT_DIR_PATH)
                .setMasterLogFile(tempFile, true)
                .setLogLevel(Level.SEVERE)
                .build());
    assertThat("Validating process return code : ", response.get().getReturnCode(), is(0));
  }

  @Test(expected = FileNotFoundException.class)
  public void startProcessWithProcessConfigWithWrongParams()
      throws IOException, ProcessConfigurationException, InterruptedException, ProcessException {
    final File tempFile =
        File.createTempFile(Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
    tempFile.setReadOnly();
    tempFile.deleteOnExit();

    ProcessRunnerFactory.startProcess(
        new ConfigurationBuilder(getDefaultInterpreter(), getInterPreterVersion())
            .setWorkigDir(Constants.DEFAULT_CURRENT_DIR_PATH)
            .setMasterLogFile(tempFile, true)
            .setLogLevel(Level.SEVERE)
            .build());
  }

  @Test(expected = FileNotFoundException.class)
  public void startThreadedProcessWithProcessConfigWithWrongParams()
      throws ProcessException, IOException, ProcessConfigurationException {
    final File tempFile =
        File.createTempFile(Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
    tempFile.setReadOnly();
    tempFile.deleteOnExit();

    ProcessRunnerFactory.startAsyncProcess(
        new ConfigurationBuilder(getDefaultInterpreter(), getInterPreterVersion())
            .setWorkigDir(Constants.DEFAULT_CURRENT_DIR_PATH)
            .setMasterLogFile(tempFile, true)
            .setLogLevel(Level.SEVERE)
            .build());
  }

  @Test
  public void getProcessLessDetailed()
      throws IOException, ProcessException, InterruptedException, ProcessConfigurationException {
    final ProcessRunner processRunner =
        ProcessRunnerFactory.getProcess(
            new ConfigurationBuilder(getDefaultInterpreter(), getInterPreterVersion())
                .setWorkigDir(Constants.DEFAULT_CURRENT_DIR_PATH)
                .setLogLevel(Level.SEVERE)
                .build());
    final Output response = processRunner.run();
    assertThat("Validating process return code : ", response.getReturnCode(), is(0));
    final File masterLog = response.getMasterLogAsJson();
    assertThat("Validating if JSON log dump is created : ", masterLog, is(nullValue()));
  }

  @Test
  public void streamingOutput()
      throws ProcessConfigurationException, IOException, InterruptedException, ProcessException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    final Output response =
        ProcessRunnerFactory.startProcess(
            new ConfigurationBuilder(getDefaultInterpreter(), getInterPreterVersion())
                .setWorkigDir(Constants.DEFAULT_CURRENT_DIR_PATH)
                .setMasterLogFile(Utilities.createTempLogDump(), true)
                .setLogLevel(Level.INFO)
                .setStramingDestination(ps)
                .build());
    String outputString =
        baos.toString(StandardCharsets.UTF_8.toString()).split(Constants.NEW_LINE)[2].substring(10);
    //assertThat(
    //    "Validating streaming log content : ", outputString, startsWith(getInitialVersionComments()));
    assertThat("Validating streaming log content : ", outputString, is(""));
  }

  private boolean isJSONValid(final String jsonInString) {
    try {
      this.gson.fromJson(jsonInString, Object.class);
      return true;
    } catch (final com.google.gson.JsonSyntaxException ex) {
      return false;
    }
  }

  private String getInterPreterVersion() {
    String message = "--version";
    if (SystemUtils.IS_OS_WINDOWS) {
      message = "ver";
    }
    return message;
  }

  private int getVersionPutputSize() {
    int response = 6;
    if (SystemUtils.IS_OS_WINDOWS) {
      response = 2;
    }
    return response;
  }

  private String getDefaultInterpreter() {
    String message = "bash";
    if (SystemUtils.IS_OS_WINDOWS) {
      message = "cmd.exe /c";
    }
    return message;
  }

  private String getInitialVersionComments() {
    String message = "GNU bash, version";
    if (SystemUtils.IS_OS_WINDOWS) {
      message = "Microsoft Windows [Version";
    }
    return message;
  }

  @Test
  public void getProcessMoreDetailed()
      throws IOException, ProcessException, InterruptedException, ProcessConfigurationException {
    final ProcessRunner processRunner =
        ProcessRunnerFactory.getProcess(
            new ConfigurationBuilder(getDefaultInterpreter(), getInterPreterVersion())
                .setWorkigDir(Constants.DEFAULT_CURRENT_DIR_PATH)
                .setMasterLogFile(
                    File.createTempFile(
                        Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON),
                    false)
                .setLogLevel(Level.SEVERE)
                .build());
    final Output response = processRunner.run();
    assertThat("Validating process return code : ", response.getReturnCode(), is(0));
    final File masterLog = response.getMasterLogAsJson();
    assertThat("Validating if JSON log dump is created : ", masterLog.exists(), is(true));
    final String jsonLogAsString =
        new Scanner(masterLog, Charset.defaultCharset().name()).useDelimiter("\\Z").next();
    final Type listTypeOuputArray = new TypeToken<List<OutputRecord>>() {}.getType();
    final List<OutputRecord> outputRecord = this.gson.fromJson(jsonLogAsString, listTypeOuputArray);
    assertThat("Is the jsonLogAsString a valid json : ", isJSONValid(jsonLogAsString), is(true));
    assertThat(
        "Validating json log record number : ", outputRecord.size(), is(getVersionPutputSize()));
    assertThat(
        "Validating json log content : ",
        outputRecord.get(this.arryPosition).getOutputText(),
        startsWith(getInitialVersionComments()));
    masterLog.delete();
  }

  @Test(expected = ProcessException.class)
  public void validateUtilitiesClassWriteLogHandlesFileExceptionProperlyOrNot()
      throws IOException, ProcessException, InterruptedException, ProcessConfigurationException {
    final ProcessRunner processRunner =
        ProcessRunnerFactory.getProcess(
            new ConfigurationBuilder(getDefaultInterpreter(), getInterPreterVersion())
                .setWorkigDir(Constants.DEFAULT_CURRENT_DIR_PATH)
                .setMasterLogFile(Utilities.createTempLogDump(), false)
                .setLogLevel(Level.SEVERE)
                .build());
    final Output response = processRunner.run();

    final File sysout =
        response.saveSysOut(
            File.createTempFile(
                Constants.FILE_PREFIX_NAME_LOG_DUMP + "-sysout", Constants.FILE_SUFFIX_JSON));
    sysout.deleteOnExit();
    sysout.setReadOnly();
    response.saveSysOut(sysout);
  }

  @Test
  public void testSaveSysOutAndSaveSysError()
      throws IOException, ProcessException, InterruptedException, ProcessConfigurationException {
    final File tempFile =
        File.createTempFile(Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();
    ProcessRunner processRunner = null;
    if (SystemUtils.IS_OS_WINDOWS) {
      processRunner =
          ProcessRunnerFactory.getProcess(
              new ConfigurationBuilder("cmd /c", "test.bat")
                  .setWorkigDir(
                      new File(
                              Constants.DEFAULT_CURRENT_DIR.getAbsolutePath()
                                  + File.separator
                                  + "src"
                                  + File.separator
                                  + "test"
                                  + File.separator
                                  + "scripts"
                                  + File.separator
                                  + "batch")
                          .toPath())
                  .setMasterLogFile(tempFile, true)
                  .setLogLevel(Level.SEVERE)
                  .build());
    } else if (SystemUtils.IS_OS_LINUX) {
      processRunner =
          ProcessRunnerFactory.getProcess(
              new ConfigurationBuilder("bash", "test.sh")
                  .setWorkigDir(
                      new File(
                              Constants.DEFAULT_CURRENT_DIR.getAbsolutePath()
                                  + File.separator
                                  + "src"
                                  + File.separator
                                  + "test"
                                  + File.separator
                                  + "scripts"
                                  + File.separator
                                  + "shell")
                          .toPath())
                  .setMasterLogFile(tempFile, true)
                  .setLogLevel(Level.SEVERE)
                  .build());
    }

    final Output response = processRunner.run();
    final File sysout =
        response.saveSysOut(
            File.createTempFile(
                Constants.FILE_PREFIX_NAME_LOG_DUMP + "-sysout", Constants.FILE_SUFFIX_JSON));
    sysout.deleteOnExit();
    final File syserr =
        response.saveSysError(
            File.createTempFile(
                Constants.FILE_PREFIX_NAME_LOG_DUMP + "-syserr", Constants.FILE_SUFFIX_JSON));
    final File masterLog = response.getMasterLogAsJson();
    masterLog.deleteOnExit();
    syserr.deleteOnExit();
    assertThat("Validating process return code : ", response.getReturnCode(), is(0));
    assertThat("Validating if JSON log dump is created : ", masterLog.exists(), is(true));
    final String jsonLogAsString =
        new Scanner(masterLog, Charset.defaultCharset().name()).useDelimiter("\\Z").next();
    final Type listTypeOuputArray = new TypeToken<List<OutputRecord>>() {}.getType();
    final List<OutputRecord> outputRecord = this.gson.fromJson(jsonLogAsString, listTypeOuputArray);
    assertThat("Is the jsonLogAsString a valid json : ", isJSONValid(jsonLogAsString), is(true));
    assertThat("Validating json log record number : ", outputRecord.size(), is(greaterThan(0)));
    assertThat(
        "Validating number of input on SYSERR : ", getFileLineNumber(syserr), is(greaterThan(0)));
    assertThat(
        "Validating number of input on SYSOUT : ", getFileLineNumber(sysout), is(greaterThan(0)));
  }

  @Test
  public void testScriptWithLargeOutput()
      throws IOException, ProcessException, InterruptedException, ProcessConfigurationException {
    final File tempFile =
        File.createTempFile(Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();
    ProcessRunner processRunner = null;
    if (SystemUtils.IS_OS_WINDOWS) {
      processRunner =
          ProcessRunnerFactory.getProcess(
              new ConfigurationBuilder("cmd /c", "largefile.bat")
                  .setWorkigDir(
                      new File(
                              Constants.DEFAULT_CURRENT_DIR.getAbsolutePath()
                                  + File.separator
                                  + "src"
                                  + File.separator
                                  + "test"
                                  + File.separator
                                  + "scripts"
                                  + File.separator
                                  + "batch")
                          .toPath())
                  .setMasterLogFile(tempFile, true)
                  .setLogLevel(Level.SEVERE)
                  .build());
    } else if (SystemUtils.IS_OS_LINUX) {
      processRunner =
          ProcessRunnerFactory.getProcess(
              new ConfigurationBuilder("bash", "largefile.sh")
                  .setWorkigDir(
                      new File(
                              Constants.DEFAULT_CURRENT_DIR.getAbsolutePath()
                                  + File.separator
                                  + "src"
                                  + File.separator
                                  + "test"
                                  + File.separator
                                  + "scripts"
                                  + File.separator
                                  + "shell")
                          .toPath())
                  .setMasterLogFile(tempFile, true)
                  .setLogLevel(Level.SEVERE)
                  .build());
    }

    final Output response = processRunner.run();
    final File sysout = response.saveSysOut(File.createTempFile("temp-file-sysout", ".json"));
    sysout.deleteOnExit();
    final File syserr = response.saveSysError(File.createTempFile("temp-file-syserr", ".json"));
    final File masterLog = response.getMasterLogAsJson();
    masterLog.deleteOnExit();
    syserr.deleteOnExit();
    assertThat("Validating process return code : ", response.getReturnCode(), is(0));
    assertThat("Validating if JSON log dump is created : ", masterLog.exists(), is(true));
    final String jsonLogAsString =
        new Scanner(masterLog, Charset.defaultCharset().name()).useDelimiter("\\Z").next();
    final Type listTypeOuputArray = new TypeToken<List<OutputRecord>>() {}.getType();
    final List<OutputRecord> outputRecord = this.gson.fromJson(jsonLogAsString, listTypeOuputArray);
    assertThat("Is the jsonLogAsString a valid json : ", isJSONValid(jsonLogAsString), is(true));
    assertThat("Validating json log record number : ", outputRecord.size(), is(greaterThan(0)));
    assertThat(
        "Validating number of input on SYSERR : ", getFileLineNumber(syserr), is(greaterThan(0)));
    assertThat(
        "Validating number of input on SYSOUT : ", getFileLineNumber(sysout), is(greaterThan(0)));
  }

  private int getFileLineNumber(final File fileToCountLineNumber) throws IOException {
    final LineNumberReader lnr = new LineNumberReader(new FileReader(fileToCountLineNumber));
    lnr.skip(Long.MAX_VALUE);
    final int lineNumber = lnr.getLineNumber() + 1; //Add 1 because line index starts at 0
    lnr.close();
    return lineNumber;
  }
}
