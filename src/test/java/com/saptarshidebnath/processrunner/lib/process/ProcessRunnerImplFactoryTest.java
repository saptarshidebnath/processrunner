package com.saptarshidebnath.processrunner.lib.process;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.saptarshidebnath.processrunner.lib.exception.ProcessConfigurationException;
import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
import com.saptarshidebnath.processrunner.lib.output.Output;
import com.saptarshidebnath.processrunner.lib.output.OutputRecord;
import com.saptarshidebnath.processrunner.lib.utilities.Constants;
import org.apache.commons.lang3.SystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringStartsWith.startsWith;

/** Created by saptarshi on 12/22/2016. */
public class ProcessRunnerImplFactoryTest {

  private final Gson gson;
  private final int arryPosition;

  {
    this.gson = new GsonBuilder().setPrettyPrinting().create();
    if (SystemUtils.IS_OS_WINDOWS) {
      this.arryPosition = 1;
    } else {
      this.arryPosition = 0;
    }
  }

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void startProcess() throws ProcessException {
    final Output response =
        ProcessRunnerFactory.startProcess(
            getDefaultInterpreter(), getInterPreterVersion(), Level.ALL);
    assertThat("Validating process runner for simple process : ", response.getReturnCode(), is(0));
  }

  @Test(expected = ProcessException.class)
  public void startProcessWithWrongParmeters() throws ProcessException {
    ProcessRunnerFactory.startProcess("", getInterPreterVersion(), Level.OFF);
  }

  @Test(expected = ProcessException.class)
  public void getProcessWithLessParamsWrongParamets() throws ProcessException {
    ProcessRunnerFactory.getProcess(
        getDefaultInterpreter(), "", Constants.DEFAULT_CURRENT_DIR, Level.OFF);
  }

  @Test(expected = ProcessException.class)
  public void getProcessMoreParamWrongParamets() throws ProcessException, IOException {
    final File tempFile =
        File.createTempFile(Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();
    ProcessRunnerFactory.getProcess(
        "", getInterPreterVersion(), Constants.DEFAULT_CURRENT_DIR, tempFile, false, Level.ALL);
  }

  @Test
  public void startProcessWithProcessConfig()
      throws ProcessException, IOException, ProcessConfigurationException {
    final File tempFile =
        File.createTempFile(Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();
    final ProcessConfiguration configuration =
        new ProcessConfiguration(
            getDefaultInterpreter(),
            getInterPreterVersion(),
            Constants.DEFAULT_CURRENT_DIR,
            tempFile,
            true,
            Level.ALL);
    final Output response = ProcessRunnerFactory.startProcess(configuration);
    assertThat("Validating process return code : ", response.getReturnCode(), is(0));
  }

  @Test
  public void searchContent()
      throws ProcessException, IOException, ProcessConfigurationException, ExecutionException,
          InterruptedException {

    final ProcessRunner processRunner =
        ProcessRunnerFactory.getProcess(
            getDefaultInterpreter(),
            getInterPreterVersion(),
            Constants.DEFAULT_CURRENT_DIR,
            Level.SEVERE);

    final Output response = processRunner.run();
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
            getDefaultInterpreter(),
            getInterPreterVersion(),
            Constants.DEFAULT_CURRENT_DIR,
            Level.SEVERE);

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
    final ProcessConfiguration configuration =
        new ProcessConfiguration(
            getDefaultInterpreter(),
            getInterPreterVersion(),
            Constants.DEFAULT_CURRENT_DIR,
            tempFile,
            true,
            Level.SEVERE);
    final Future<Output> response = ProcessRunnerFactory.startProcess(configuration, true);
    assertThat("Validating process return code : ", response.get().getReturnCode(), is(0));
  }

  @Test(expected = ProcessException.class)
  public void startProcessWithProcessConfigWithWrongParams()
      throws ProcessException, IOException, ProcessConfigurationException {
    final File tempFile =
        File.createTempFile(Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
    tempFile.setReadOnly();
    tempFile.deleteOnExit();
    final ProcessConfiguration configuration =
        new ProcessConfiguration(
            getDefaultInterpreter(),
            getInterPreterVersion(),
            Constants.DEFAULT_CURRENT_DIR,
            tempFile,
            true,
            Level.SEVERE);
    ProcessRunnerFactory.startProcess(configuration);
  }

  @Test(expected = ProcessException.class)
  public void startThreadedProcessWithProcessConfigWithWrongParams()
      throws ProcessException, IOException, ProcessConfigurationException {
    final File tempFile =
        File.createTempFile(Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
    tempFile.setReadOnly();
    tempFile.deleteOnExit();
    final ProcessConfiguration configuration =
        new ProcessConfiguration(
            getDefaultInterpreter(),
            getInterPreterVersion(),
            Constants.DEFAULT_CURRENT_DIR,
            tempFile,
            true,
            Level.SEVERE);
    ProcessRunnerFactory.startProcess(configuration, true);
  }

  @Test
  public void getProcessLessDetailed() throws IOException, ProcessException, InterruptedException {
    final ProcessRunner processRunner =
        ProcessRunnerFactory.getProcess(
            getDefaultInterpreter(),
            getInterPreterVersion(),
            Constants.DEFAULT_CURRENT_DIR,
            Level.SEVERE);
    final Output response = processRunner.run();
    assertThat("Validating process return code : ", response.getReturnCode(), is(0));
    final File masterLog = response.getMasterLog();
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
  public void getProcessMoreDetailed() throws IOException, ProcessException, InterruptedException {
    final ProcessRunner processRunner =
        ProcessRunnerFactory.getProcess(
            getDefaultInterpreter(),
            getInterPreterVersion(),
            Constants.DEFAULT_CURRENT_DIR,
            File.createTempFile(Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON),
            false,
            Level.SEVERE);
    final Output response = processRunner.run();
    assertThat("Validating process return code : ", response.getReturnCode(), is(0));
    final File masterLog = response.getMasterLog();
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
      throws IOException, ProcessException, InterruptedException {
    final ProcessRunner processRunner =
        ProcessRunnerFactory.getProcess(
            getDefaultInterpreter(),
            getInterPreterVersion(),
            Constants.DEFAULT_CURRENT_DIR,
            File.createTempFile(Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON),
            false,
            Level.SEVERE);
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
      throws IOException, ProcessException, InterruptedException {
    final File tempFile =
        File.createTempFile(Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();
    ProcessRunner processRunner = null;
    if (SystemUtils.IS_OS_WINDOWS) {
      processRunner =
          ProcessRunnerFactory.getProcess(
              "cmd /c",
              "test.bat",
              new File(
                  Constants.DEFAULT_CURRENT_DIR.getAbsolutePath()
                      + File.separator
                      + "src"
                      + File.separator
                      + "test"
                      + File.separator
                      + "scripts"
                      + File.separator
                      + "batch"),
              tempFile,
              true,
              Level.SEVERE);
    } else if (SystemUtils.IS_OS_LINUX) {
      processRunner =
          ProcessRunnerFactory.getProcess(
              "bash",
              "test.sh",
              new File(
                  Constants.DEFAULT_CURRENT_DIR.getAbsolutePath()
                      + File.separator
                      + "src"
                      + File.separator
                      + "test"
                      + File.separator
                      + "scripts"
                      + File.separator
                      + "shell"),
              tempFile,
              true,
              Level.SEVERE);
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
    final File masterLog = response.getMasterLog();
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
      throws IOException, ProcessException, InterruptedException {
    final File tempFile =
        File.createTempFile(Constants.FILE_PREFIX_NAME_LOG_DUMP, Constants.FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();
    ProcessRunner processRunner = null;
    if (SystemUtils.IS_OS_WINDOWS) {
      processRunner =
          ProcessRunnerFactory.getProcess(
              "cmd /c",
              "largefile.bat",
              new File(
                  Constants.DEFAULT_CURRENT_DIR.getAbsolutePath()
                      + File.separator
                      + "src"
                      + File.separator
                      + "test"
                      + File.separator
                      + "scripts"
                      + File.separator
                      + "batch"),
              tempFile,
              true,
              Level.SEVERE);
    } else if (SystemUtils.IS_OS_LINUX) {
      processRunner =
          ProcessRunnerFactory.getProcess(
              "bash",
              "largefile.sh",
              new File(
                  Constants.DEFAULT_CURRENT_DIR.getAbsolutePath()
                      + File.separator
                      + "src"
                      + File.separator
                      + "test"
                      + File.separator
                      + "scripts"
                      + File.separator
                      + "shell"),
              tempFile,
              true,
              Level.SEVERE);
    }

    final Output response = processRunner.run();
    final File sysout = response.saveSysOut(File.createTempFile("temp-file-sysout", ".json"));
    sysout.deleteOnExit();
    final File syserr = response.saveSysError(File.createTempFile("temp-file-syserr", ".json"));
    final File masterLog = response.getMasterLog();
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
