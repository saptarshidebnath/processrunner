package com.saptarshidebnath.processrunner.lib.process;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.saptarshidebnath.processrunner.lib.exception.ProcessException;
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
    final int response =
        ProcessRunnerFactory.startProcess(getDefaultInterpreter(), getInterPreterVersion());
    assertThat("Validating process runner for simple process : ", response, is(0));
  }

  @Test(expected = ProcessException.class)
  public void startProcessWithWrongParmeters() throws ProcessException {
    ProcessRunnerFactory.startProcess("", getInterPreterVersion());
  }

  @Test(expected = ProcessException.class)
  public void getProcessWithWrongParamets() throws ProcessException {
    final ProcessRunner processRunner =
        ProcessRunnerFactory.getProcess(getDefaultInterpreter(), "", Constants.DEFAULT_CURRENT_DIR);
  }

  @Test
  public void getProcessLessDetailed() throws IOException, ProcessException, InterruptedException {
    final ProcessRunner processRunner =
        ProcessRunnerFactory.getProcess(
            getDefaultInterpreter(), getInterPreterVersion(), Constants.DEFAULT_CURRENT_DIR);
    final int response = processRunner.run();
    assertThat("Validating process return code : ", response, is(0));
    final File jsonLogDump = processRunner.getJsonLogDump();
    assertThat("Validating if JSON log dump is created : ", jsonLogDump.exists(), is(true));
    final String jsonLogAsString =
        new Scanner(jsonLogDump, Charset.defaultCharset().name()).useDelimiter("\\Z").next();
    final Type listTypeOuputArray = new TypeToken<List<Output>>() {}.getType();
    final List<Output> output = this.gson.fromJson(jsonLogAsString, listTypeOuputArray);
    assertThat("Is the jsonLogAsString a valid json : ", isJSONValid(jsonLogAsString), is(true));
    assertThat("Validating json log record number : ", output.size(), is(getVersionPutputSize()));
    assertThat(
        "Validating json log content : ",
        output.get(this.arryPosition).getOutputText(),
        startsWith(getInitialVersionComments()));
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
    assertThat("Validating json log record number : ", output.size(), is(getVersionPutputSize()));

    assertThat(
        "Validating json log content : ",
        output.get(this.arryPosition).getOutputText(),
        startsWith(getInitialVersionComments()));
    jsonLogDump.delete();
  }
}
