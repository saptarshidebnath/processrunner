package com.saptarshidebnath.lib.processrunner.ouput;

import static com.saptarshidebnath.lib.processrunner.utilities.Constants.FILE_PREFIX_NAME_LOG_DUMP;
import static com.saptarshidebnath.lib.processrunner.utilities.Constants.FILE_SUFFIX_JSON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.saptarshidebnath.lib.processrunner.exception.ProcessConfigurationException;
import com.saptarshidebnath.lib.processrunner.exception.ProcessException;
import com.saptarshidebnath.lib.processrunner.output.Output;
import com.saptarshidebnath.lib.processrunner.output.OutputSourceType;
import com.saptarshidebnath.lib.processrunner.process.Configuration;
import com.saptarshidebnath.lib.processrunner.process.ConfigurationBuilder;
import com.saptarshidebnath.lib.processrunner.process.ProcessRunnerFactory;
import com.saptarshidebnath.lib.processrunner.utilities.Constants;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.Test;

/** Created by saptarshi on 7/4/2017. */
public class OutputTest {

  @Test
  public void validateOutputTest() {
    assertThat(
        "Validating OutputSourceType enum : ",
        OutputSourceType.valueOf("SYSOUT"),
        is(OutputSourceType.SYSOUT));
    assertThat(
        "Validating OutputSourceType enum : ",
        OutputSourceType.valueOf("SYSERR"),
        is(OutputSourceType.SYSERR));
  }

  @Test(expected = ProcessConfigurationException.class)
  public void saveSysoutWithoutMasterLogFile()
      throws ProcessConfigurationException, InterruptedException, ExecutionException, IOException,
          ProcessException {
    final File tempFile = File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();
    Output output =
        ProcessRunnerFactory.startProcess(
            new ConfigurationBuilder("bash", "echo Hi This is Saptarshi").build());
    //
    // Should generate an error as Master log file is not set.
    //
    output.saveSysOut(tempFile);
  }

  @Test(expected = ProcessConfigurationException.class)
  public void saveSyserrWithoutMasterLogFile()
      throws ProcessConfigurationException, InterruptedException, ExecutionException, IOException,
          ProcessException {
    final File tempFile = File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();
    Output output =
        ProcessRunnerFactory.startProcess(
            new ConfigurationBuilder("bash", "echo Hi This is Saptarshi").build());
    //
    // Should generate an error as Master log file is not set.
    //
    output.saveSysError(tempFile);
  }

  @Test(expected = ProcessConfigurationException.class)
  public void saveMasterLogWithoutMasterLogFile()
      throws ProcessConfigurationException, InterruptedException, ExecutionException, IOException {
    final File tempFile = File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();
    Output output =
        ProcessRunnerFactory.startProcess(
            new ConfigurationBuilder("bash", "echo Hi This is Saptarshi").build());
    //
    // Should generate an error as Master log file is not set.
    //
    output.saveLog(tempFile);
  }

  @Test
  public void saveMasterLogFile()
      throws ProcessConfigurationException, InterruptedException, ExecutionException, IOException {
    final File tempFile = File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();
    String toOutput = "Hi This is Saptarshi";
    Output output =
        ProcessRunnerFactory.startProcess(
            new ConfigurationBuilder("/bin/echo", toOutput)
                .setMasterLogFile(
                    File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, FILE_SUFFIX_JSON), Boolean.TRUE)
                .build());
    //
    // Should generate an error as Master log file is not set.
    //
    List<String> fileContains = Files.readAllLines(output.saveLog(tempFile).toPath());
    assertThat("Number of lines in the File", fileContains.size(), is(1));
    assertThat("Content of the FIle is ", fileContains.get(0), is(toOutput));
  }

  @Test
  public void seachMasterLogFile()
      throws ProcessConfigurationException, InterruptedException, ExecutionException, IOException {
    final File tempFile = File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, FILE_SUFFIX_JSON);
    tempFile.deleteOnExit();
    String toOutput = "Hi This is Saptarshi";
    Output output =
        ProcessRunnerFactory.startProcess(
            new ConfigurationBuilder("/bin/echo", toOutput)
                .setMasterLogFile(
                    File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, FILE_SUFFIX_JSON), Boolean.TRUE)
                .build());
    assertThat("Regex matching", output.searchMasterLog(".*Sap.*"), is(Boolean.TRUE));
    assertThat("Regex not matching", output.searchMasterLog(".*Saptarshii"), is(Boolean.FALSE));
  }

  @Test
  public void seachMasterLogFileForRegex()
      throws IOException, ExecutionException, ProcessConfigurationException, InterruptedException {
    final File tempFile = File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, FILE_SUFFIX_JSON);
    Output output =
        ProcessRunnerFactory.startProcess(
            new ConfigurationBuilder("/bin/bash -c", "ls -la")
                .enableLogStreaming(true)
                .setMasterLogFile(tempFile, true)
                .build());
    assertThat(
        "Validating for output", output.grepForRegex(".*").get(0).getOutputText(), is("CNAME"));
    assertThat(
        "Validating for output", output.grepForRegex("s[r|o]c").get(0).getOutputText(), is("src"));
    assertThat(
        "Validating for output",
        output.grepForRegex("pom.*").get(0).getOutputText(),
        is("pom.xml"));
  }

  @Test(expected = ProcessConfigurationException.class)
  public void seachMasterLogFileForRegexWithoutMasterLogFile()
      throws IOException, ExecutionException, ProcessConfigurationException, InterruptedException {
    Output output =
        ProcessRunnerFactory.startProcess(
            new ConfigurationBuilder("/bin/bash -c", "ls -la").enableLogStreaming(true).build());
    //
    // Should generate exception as masterLogFile is not set.
    //
    output.grepForRegex(".*");
  }

  @Test(expected = ProcessConfigurationException.class)
  public void seachMasterLogFileWithoutMasterLogFile()
      throws IOException, ExecutionException, ProcessConfigurationException, InterruptedException {
    String toOutput = "Hi This is Saptarshi";
    Output output =
        ProcessRunnerFactory.startProcess(new ConfigurationBuilder("/bin/echo", toOutput).build());
    output.searchMasterLog(".*Sap.*");
  }

  @Test
  public void toStringTest()
      throws IOException, ExecutionException, ProcessConfigurationException, InterruptedException {
    final File tempFile = File.createTempFile(FILE_PREFIX_NAME_LOG_DUMP, FILE_SUFFIX_JSON);
    String interPreter = "/bin/echo";
    String command = "Saptarshi";
    String param = "Debnath";
    Path workingDir = new File(Constants.USER_DIR).toPath();
    Configuration configuration =
        new ConfigurationBuilder(interPreter, command)
            .enableLogStreaming(Boolean.TRUE)
            .setMasterLogFile(tempFile, Boolean.TRUE)
            .setParam(param)
            .setWorkigDir(workingDir)
            .build();
    Output output = ProcessRunnerFactory.startProcess(configuration);
    String outputtoString = output.toString();
    String expectedValue =
        "OutputImpl{configuration=Configuration{interpreter='"
            + interPreter
            + "', command='"
            + command
            + " "
            + param
            + "', workingDir="
            + workingDir.toString()
            + ", masterLogFile="
            + tempFile.getCanonicalPath()
            + ", charset=UTF-8, autoDeleteFileOnExit=true, enableLogStreaming=true}, returnCode=0}";
    assertThat("Output to string", outputtoString, is(expectedValue));
  }
}
