package com.saptarshidebnath.lib.processrunner.process;

import com.saptarshidebnath.lib.processrunner.configuration.Configuration.ConfigBuilder;
import com.saptarshidebnath.lib.processrunner.constants.ProcessRunnerConstants;
import com.saptarshidebnath.lib.processrunner.exception.ProcessConfigurationException;
import java.io.File;
import org.junit.Test;

public class ConfigBuilderTest {
  @Test(expected = ProcessConfigurationException.class)
  public void setMasterLogFileTest() throws ProcessConfigurationException {
    new ConfigBuilder("abc", "def")
        .setMasterLogFile(new File(ProcessRunnerConstants.USER_DIR), Boolean.FALSE);
  }
}
