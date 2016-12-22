package com.saptarshidebnath.processrunner.lib.process;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.saptarshidebnath.processrunner.lib.exception.ProcessConfigurationException;
import com.saptarshidebnath.processrunner.lib.output.Output;
import com.saptarshidebnath.processrunner.lib.utilities.Constants;
import org.apache.commons.lang3.SystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Scanner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/** Created by saptarshi on 12/22/2016. */
public class ProcessRunnerImplFactoryTest {

  private final Gson gson;

  {
    this.gson = new GsonBuilder().setPrettyPrinting().create();
  }

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void startProcess()
      throws InterruptedException, ProcessConfigurationException, IOException {
    final int response =
        ProcessRunnerFactory.startProcess(getDefaultInterpreter(), "echo Saptarshi");
    assertThat("Validating process runner for simple process : ", response, is(0));
  }

  @Test
  public void getProcessLessDetailed()
      throws IOException, ProcessConfigurationException, InterruptedException {
    final ProcessRunner processRunner =
        ProcessRunnerFactory.getProcess(
            getDefaultInterpreter(), "echo Saptarshi Debnath", Constants.DEFAULT_CURRENT_DIR);
    final int response = processRunner.run();
    assertThat("Validating process return code : ", response, is(0));
    final File jsonLogDump = processRunner.getJsonLogDump();
    assertThat("Validating if JSON log dump is created : ", jsonLogDump.exists(), is(true));
    final String jsonLogAsString =
        new Scanner(jsonLogDump, Charset.defaultCharset().name()).useDelimiter("\\Z").next();
    final Type listTypeOuputArray = new TypeToken<List<Output>>() {}.getType();
    final List<Output> output = this.gson.fromJson(jsonLogAsString, listTypeOuputArray);
    assertThat("Is the jsonLogAsString a valid json : ", isJSONValid(jsonLogAsString), is(true));
    assertThat("Validating json log record number : ", output.size(), is(1));
    assertThat(
        "Validating json log content : ", output.get(0).getOutputText(), is("Saptarshi Debnath"));
    //TODO
    //Add test case for sys file and sys error file creation.
    jsonLogDump.delete();
  }

  private boolean isJSONValid(final String jsonInString) {
    try {
      this.gson.fromJson(jsonInString, Object.class);
      return true;
    } catch (final com.google.gson.JsonSyntaxException ex) {
      return false;
    }
  }

  private String getDefaultInterpreter() {
    String message = "bash";
    if (SystemUtils.IS_OS_WINDOWS) {
      message = "cmd.exe /c";
    }
    return message;
  }

  @Test
  public void getProcessMoreDetailed()
      throws IOException, ProcessConfigurationException, InterruptedException {
    final ProcessRunner processRunner =
        ProcessRunnerFactory.getProcess(
            getDefaultInterpreter(),
            "echo SapDev",
            Constants.DEFAULT_CURRENT_DIR,
            File.createTempFile("temp-file-name", ".json"),
            false);
    final int response = processRunner.run();
    assertThat("Validating process return code : ", response, is(0));
    final File jsonLogDump = processRunner.getJsonLogDump();
    assertThat("Validating if JSON log dump is created : ", jsonLogDump.exists(), is(true));
    final String jsonLogAsString =
        new Scanner(jsonLogDump, Charset.defaultCharset().name()).useDelimiter("\\Z").next();
    final Type listTypeOuputArray = new TypeToken<List<Output>>() {}.getType();
    final List<Output> output = this.gson.fromJson(jsonLogAsString, listTypeOuputArray);
    assertThat("Is the jsonLogAsString a valid json : ", isJSONValid(jsonLogAsString), is(true));
    assertThat("Validating json log record number : ", output.size(), is(1));
    assertThat("Validating json log content : ", output.get(0).getOutputText(), is("SapDev"));
    //TODO
    //Add test case for sys file and sys error file creation.
    jsonLogDump.delete();
  }
}
