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

import static com.saptarshidebnath.lib.processrunner.constants.ProcessRunnerConstants.GSON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.StringStartsWith.startsWith;

import com.saptarshidebnath.lib.processrunner.configuration.Configuration;
import com.saptarshidebnath.lib.processrunner.configuration.Configuration.ConfigBuilder;
import com.saptarshidebnath.lib.processrunner.constants.ProcessRunnerConstants;
import com.saptarshidebnath.lib.processrunner.exception.ProcessConfigurationException;
import com.saptarshidebnath.lib.processrunner.exception.ProcessException;
import com.saptarshidebnath.lib.processrunner.model.OutputRecord;
import com.saptarshidebnath.lib.processrunner.output.Output;
import com.saptarshidebnath.lib.processrunner.utilities.fileutils.TempFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

/** Created by saptarshi on 12/22/2016. */
public class RunnerFactoryTest {

  private final int arryPosition;

  public RunnerFactoryTest() {
    if (SystemUtils.IS_OS_WINDOWS) {
      this.arryPosition = 1;
    } else {
      this.arryPosition = 0;
    }
  }

  @Test
  public void startProcess()
      throws ProcessConfigurationException, IOException, InterruptedException, ExecutionException {
    final Output response =
        RunnerFactory.startProcess(
            new ConfigBuilder(getDefaultInterpreter(), getInterPreterVersion()).build());
    assertThat("Validating process runner for simple process : ", response.getReturnCode(), is(0));
  }

  @Test(expected = ProcessConfigurationException.class)
  public void startProcessWithWrongParmeters()
      throws ProcessConfigurationException, IOException, InterruptedException, ExecutionException {
    RunnerFactory.startProcess(new ConfigBuilder("", getInterPreterVersion()).build());
  }

  @Test(expected = ProcessConfigurationException.class)
  public void getProcessWithLessParamsWrongParamets() throws ProcessConfigurationException {
    new ConfigBuilder(getDefaultInterpreter(), "").build();
  }

  @Test(expected = ProcessConfigurationException.class)
  public void getProcessMoreParamWrongParamets()
      throws IOException, ProcessConfigurationException, InterruptedException, ExecutionException {
    final File tempFile =
        File.createTempFile(
            ProcessRunnerConstants.FILE_PREFIX_NAME_LOG_DUMP,
            ProcessRunnerConstants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();

    RunnerFactory.startProcess(
        new ConfigBuilder("", getInterPreterVersion())
            .setWorkigDir(ProcessRunnerConstants.DEFAULT_CURRENT_DIR_PATH)
            .setMasterLogFile(tempFile, false)
            .build());
  }

  @Test
  public void startProcessWithProcessConfig()
      throws IOException, ProcessConfigurationException, InterruptedException, ExecutionException {
    final File tempFile =
        File.createTempFile(
            ProcessRunnerConstants.FILE_PREFIX_NAME_LOG_DUMP,
            ProcessRunnerConstants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();

    final Output response =
        RunnerFactory.startProcess(
            new ConfigBuilder(getDefaultInterpreter(), getInterPreterVersion())
                .setWorkigDir(ProcessRunnerConstants.DEFAULT_CURRENT_DIR_PATH)
                .setMasterLogFile(tempFile, true)
                .build());
    assertThat("Validating process return code : ", response.getReturnCode(), is(0));
  }

  @Test
  public void searchContent()
      throws ProcessConfigurationException, IOException, InterruptedException, ExecutionException {

    final Output response =
        RunnerFactory.startProcess(
            new ConfigBuilder(getDefaultInterpreter(), getInterPreterVersion())
                .setWorkigDir(ProcessRunnerConstants.DEFAULT_CURRENT_DIR_PATH)
                .setMasterLogFile(new TempFile().createTempLogDump(), true)
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
      throws ProcessConfigurationException, IOException, InterruptedException, ExecutionException {

    final Runner runner =
        RunnerFactory.getProcess(
            new ConfigBuilder(getDefaultInterpreter(), getInterPreterVersion())
                .setWorkigDir(ProcessRunnerConstants.DEFAULT_CURRENT_DIR_PATH)
                .setMasterLogFile(new TempFile().createTempLogDump(), true)
                .build());

    final Output response = runner.run();
    assertThat("Validating process return code : ", response.getReturnCode(), is(0));

    assertThat(
        "Validating searchMasterLog result for content in the output in UNIX : ",
        response.searchMasterLog("Saptarshi"),
        is(false));
  }

  @Test
  public void startThreadedProcessWithProcessConfig()
      throws IOException, ProcessConfigurationException, ExecutionException, InterruptedException {
    final File tempFile =
        File.createTempFile(
            ProcessRunnerConstants.FILE_PREFIX_NAME_LOG_DUMP,
            ProcessRunnerConstants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();

    final Future<Output> response =
        RunnerFactory.startAsyncProcess(
            new ConfigBuilder(getDefaultInterpreter(), getInterPreterVersion())
                .setWorkigDir(ProcessRunnerConstants.DEFAULT_CURRENT_DIR_PATH)
                .setMasterLogFile(tempFile, true)
                .build());
    assertThat("Validating process return code : ", response.get().getReturnCode(), is(0));
  }

  @Test(expected = ProcessConfigurationException.class)
  public void startProcessWithProcessConfigWithWrongParams()
      throws IOException, ProcessConfigurationException, InterruptedException, ExecutionException {
    final File tempFile =
        File.createTempFile(
            ProcessRunnerConstants.FILE_PREFIX_NAME_LOG_DUMP,
            ProcessRunnerConstants.FILE_SUFFIX_JSON);
    if (!tempFile.setReadOnly()) {
      throw new RuntimeException(
          "Unable to set file : " + tempFile.getAbsolutePath() + " as readonly.");
    }
    tempFile.deleteOnExit();

    RunnerFactory.startProcess(
        new ConfigBuilder(getDefaultInterpreter(), getInterPreterVersion())
            .setWorkigDir(ProcessRunnerConstants.DEFAULT_CURRENT_DIR_PATH)
            .setMasterLogFile(tempFile, true)
            .build());
  }

  @Test(expected = ProcessConfigurationException.class)
  public void startThreadedProcessWithProcessConfigWithWrongParams()
      throws IOException, ProcessConfigurationException {
    final File tempFile =
        File.createTempFile(
            ProcessRunnerConstants.FILE_PREFIX_NAME_LOG_DUMP,
            ProcessRunnerConstants.FILE_SUFFIX_JSON);
    if (!tempFile.setReadOnly()) {
      throw new RuntimeException(
          "Unable to set file : " + tempFile.getAbsolutePath() + " as readonly.");
    }
    tempFile.deleteOnExit();

    RunnerFactory.startAsyncProcess(
        new ConfigBuilder(getDefaultInterpreter(), getInterPreterVersion())
            .setWorkigDir(ProcessRunnerConstants.DEFAULT_CURRENT_DIR_PATH)
            .setMasterLogFile(tempFile, true)
            .build());
  }

  @Test(expected = ProcessConfigurationException.class)
  public void getProcessLessDetailed()
      throws IOException, InterruptedException, ProcessConfigurationException, ExecutionException {
    final Runner runner =
        RunnerFactory.getProcess(
            new ConfigBuilder(getDefaultInterpreter(), getInterPreterVersion())
                .setWorkigDir(ProcessRunnerConstants.DEFAULT_CURRENT_DIR_PATH)
                .build());
    final Output response = runner.run();
    assertThat("Validating process return code : ", response.getReturnCode(), is(0));
    //
    // This line should generate an error as master log file have not been saved.
    //
    // noinspection unused
    final File masterLog = response.getMasterLogAsJson();
  }

  // @Test
  // public void streamingOutput()
  // throws ProcessConfigurationException, IOException, InterruptedException,
  // ProcessException
  // {
  // ByteArrayOutputStream baos = new ByteArrayOutputStream();
  // RunnerFactory.startProcess(
  // new ConfigBuilder(getDefaultInterpreter(), getInterPreterVersion())
  // .setWorkigDir(ProcessRunnerConstants.DEFAULT_CURRENT_DIR_PATH)
  // .setMasterLogFile(StringUtils.createTempLogDump(), true)
  // .enableLogStreaming(true)
  // .build());
  // String outputString = null;
  // if (SystemUtils.IS_OS_WINDOWS) {
  // outputString =
  // baos.toString(StandardCharsets.UTF_8.toString())
  // .split(ProcessRunnerConstants.NEW_LINE)[2]
  // .substring(10);
  // } else {
  // outputString = baos.toString(StandardCharsets.UTF_8.toString());
  // // .split(ProcessRunnerConstants.NEW_LINE)[1]
  // // .substring(10);
  // System.out.println(outputString);
  // }
  //
  // assertThat(
  // "Validating streaming log content : ",
  // outputString,
  // startsWith(getInitialVersionComments()));
  // // assertThat("Validating streaming log content : ", outputString, is(""));
  // }

  private boolean isJSONValid(final String jsonInString) {
    try {
      GSON.fromJson(jsonInString, Object.class);
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
      throws IOException, InterruptedException, ProcessConfigurationException, ExecutionException {
    Configuration configuration =
        new ConfigBuilder(getDefaultInterpreter(), getInterPreterVersion())
            .setWorkigDir(ProcessRunnerConstants.DEFAULT_CURRENT_DIR_PATH)
            .setMasterLogFile(
                File.createTempFile(
                    ProcessRunnerConstants.FILE_PREFIX_NAME_LOG_DUMP,
                    ProcessRunnerConstants.FILE_SUFFIX_JSON),
                false)
            .build();
    final Runner runner = RunnerFactory.getProcess(configuration);
    final Output response = runner.run();
    assertThat("Validating process return code : ", response.getReturnCode(), is(0));
    final File masterLog = response.getMasterLogAsJson();
    assertThat("Validating if JSON log dump is created : ", masterLog.exists(), is(true));

    List<OutputRecord> outPutRecordList =
        Files.lines(
                Paths.get(configuration.getMasterLogFile().getCanonicalPath()),
                configuration.getCharset())
            .map(line -> GSON.fromJson(line, OutputRecord.class))
            .collect(Collectors.toList());
    assertThat(
        "Validating json log record number : ",
        outPutRecordList.size(),
        is(getVersionPutputSize()));
    assertThat(
        "Validating json log content : ",
        outPutRecordList.get(this.arryPosition).getOutputText(),
        startsWith(getInitialVersionComments()));
    assertThat("Checking if master file gets deleted or not", masterLog.delete(), is(true));
  }

  @Test(expected = IOException.class)
  public void validateUtilitiesClassWriteLogHandlesFileExceptionProperlyOrNot()
      throws IOException, ProcessException, InterruptedException, ProcessConfigurationException,
          ExecutionException {
    final Runner runner =
        RunnerFactory.getProcess(
            new ConfigBuilder(getDefaultInterpreter(), getInterPreterVersion())
                .setWorkigDir(ProcessRunnerConstants.DEFAULT_CURRENT_DIR_PATH)
                .setMasterLogFile(new TempFile().createTempLogDump(), true)
                .build());
    final Output response = runner.run();
    final File sysout =
        response.saveSysOut(
            File.createTempFile(
                ProcessRunnerConstants.FILE_PREFIX_NAME_LOG_DUMP + "-sysout",
                ProcessRunnerConstants.FILE_SUFFIX_JSON));
    sysout.deleteOnExit();
    assertThat(
        "Validating if sysout file is set as readonly or not", sysout.setReadOnly(), is(true));
    response.saveSysOut(sysout);
  }

  @Test
  public void testSaveSysOutAndSaveSysError()
      throws IOException, ProcessException, InterruptedException, ProcessConfigurationException,
          ExecutionException {
    final File tempFile =
        File.createTempFile(
            ProcessRunnerConstants.FILE_PREFIX_NAME_LOG_DUMP,
            ProcessRunnerConstants.FILE_SUFFIX_JSON);
    Runner runner = null;
    Configuration configuration = null;
    if (SystemUtils.IS_OS_WINDOWS) {
      configuration =
          new ConfigBuilder("cmd /c", "test.bat")
              .enableLogStreaming(Boolean.TRUE)
              .setWorkigDir(
                  new File(
                          ProcessRunnerConstants.DEFAULT_CURRENT_DIR.getAbsolutePath()
                              + File.separator
                              + "src"
                              + File.separator
                              + "test"
                              + File.separator
                              + "scripts"
                              + File.separator
                              + "batch")
                      .toPath())
              .setMasterLogFile(tempFile, Boolean.TRUE)
              .build();
      runner = RunnerFactory.getProcess(configuration);
    } else if (SystemUtils.IS_OS_LINUX) {
      System.out.println("LINUX");
      configuration =
          new ConfigBuilder("bash", "test.sh")
              .enableLogStreaming(Boolean.TRUE)
              .setWorkigDir(
                  new File(
                          ProcessRunnerConstants.DEFAULT_CURRENT_DIR.getAbsolutePath()
                              + File.separator
                              + "src"
                              + File.separator
                              + "test"
                              + File.separator
                              + "scripts"
                              + File.separator
                              + "shell")
                      .toPath())
              .setMasterLogFile(tempFile, Boolean.TRUE)
              .build();
      runner = RunnerFactory.getProcess(configuration);
    }
    assertThat("Validating if processrunner got created or not", runner, not(nullValue()));
    assert runner != null;
    final Output response = runner.run();
    final File masterLog = response.getMasterLogAsJson();
    // masterLog.deleteOnExit();
    final File sysout =
        response.saveSysOut(
            File.createTempFile(
                ProcessRunnerConstants.FILE_PREFIX_NAME_LOG_DUMP + "-sysout-",
                ProcessRunnerConstants.FILE_SUFFIX_JSON));
    sysout.deleteOnExit();
    final File syserr =
        response.saveSysError(
            File.createTempFile(
                ProcessRunnerConstants.FILE_PREFIX_NAME_LOG_DUMP + "-syserr-",
                ProcessRunnerConstants.FILE_SUFFIX_JSON));
    syserr.deleteOnExit();
    assertThat("Validating process return code : ", response.getReturnCode(), is(0));
    assertThat("Validating if JSON log dump is created : ", masterLog.exists(), is(true));

    List<OutputRecord> jsonLog =
        Files.lines(Paths.get(masterLog.getCanonicalPath()), configuration.getCharset())
            .map(line -> GSON.fromJson(line, OutputRecord.class))
            .collect(Collectors.toList());

    assertThat("Validating json log record number : ", jsonLog.size(), is(greaterThan(0)));
    assertThat(
        "Validating number of input on SYSERR : ", getFileLineNumber(syserr), is(greaterThan(0)));
    assertThat(
        "Validating number of input on SYSOUT : ", getFileLineNumber(sysout), is(greaterThan(0)));
  }

  @Test
  public void testScriptWithLargeOutput()
      throws IOException, ProcessException, InterruptedException, ProcessConfigurationException,
          ExecutionException {
    final File tempFile =
        File.createTempFile(
            ProcessRunnerConstants.FILE_PREFIX_NAME_LOG_DUMP,
            ProcessRunnerConstants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();
    Configuration configuration = null;
    Runner runner = null;
    if (SystemUtils.IS_OS_WINDOWS) {
      configuration =
          new ConfigBuilder("cmd /c", "largefile.bat")
              .setWorkigDir(
                  new File(
                          ProcessRunnerConstants.DEFAULT_CURRENT_DIR.getAbsolutePath()
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
              .build();
      runner = RunnerFactory.getProcess(configuration);
    } else if (SystemUtils.IS_OS_LINUX) {
      configuration =
          new ConfigBuilder("bash", "largefile.sh")
              .setWorkigDir(
                  new File(
                          ProcessRunnerConstants.DEFAULT_CURRENT_DIR.getAbsolutePath()
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
              .build();
      runner = RunnerFactory.getProcess(configuration);
    }

    assertThat("Validating if processrunner is not null", runner, not(nullValue()));
    assert runner != null;
    final Output response = runner.run();
    final File sysout = response.saveSysOut(File.createTempFile("temp-file-sysout", ".json"));
    sysout.deleteOnExit();
    final File syserr = response.saveSysError(File.createTempFile("temp-file-syserr", ".json"));
    final File masterLog = response.getMasterLogAsJson();
    masterLog.deleteOnExit();
    syserr.deleteOnExit();
    assertThat("Validating process return code : ", response.getReturnCode(), is(0));
    assertThat("Validating if JSON log dump is created : ", masterLog.exists(), is(true));
    List<OutputRecord> jsonLog =
        Files.lines(Paths.get(masterLog.getCanonicalPath()), configuration.getCharset())
            .map(line -> GSON.fromJson(line, OutputRecord.class))
            .collect(Collectors.toList());
    assertThat("Validating json log record number : ", jsonLog.size(), is(greaterThan(0)));
    assertThat(
        "Validating number of input on SYSERR : ", getFileLineNumber(syserr), is(greaterThan(0)));
    assertThat(
        "Validating number of input on SYSOUT : ", getFileLineNumber(sysout), is(greaterThan(0)));
  }

  private int getFileLineNumber(final File fileToCountLineNumber) throws IOException {
    LineNumberReader lnr = null;
    int lineNumber;
    try {
      lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(fileToCountLineNumber)));
      long skipValue = Long.MAX_VALUE;
      while (skipValue != 0) {
        skipValue = lnr.skip(Long.MAX_VALUE);
      }
      lineNumber = lnr.getLineNumber() + 1; // Add 1 because line index starts at 0
    } finally {
      if (lnr != null) {
        lnr.close();
      }
    }
    return lineNumber;
  }
}
