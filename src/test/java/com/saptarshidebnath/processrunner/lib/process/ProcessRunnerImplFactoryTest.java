package com.saptarshidebnath.processrunner.lib.process;

import com.saptarshidebnath.processrunner.lib.exception.ProcessConfigurationException;
import com.saptarshidebnath.processrunner.lib.utilities.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/** Created by saptarshi on 12/22/2016. */
public class ProcessRunnerFactoryTest {

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void startProcess()
      throws InterruptedException, ProcessConfigurationException, IOException {
    final int response = ProcessRunnerFactory.startProcess("", "echo Saptarshi");
    assertThat("Validating process runner for simple process : ", response, is(0));
  }

  @Test
  public void getProcess() throws IOException, ProcessConfigurationException {
    final ProcessRunnerInterface processRunner =
        ProcessRunnerFactory.getProcess(
            "", "echo Saptarshi Debnath", Constants.DEFAULT_CURRENT_DIR);
  }

  @Test
  public void getProcess1() throws Exception {}
}
