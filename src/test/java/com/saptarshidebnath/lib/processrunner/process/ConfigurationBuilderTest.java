package com.saptarshidebnath.lib.processrunner.process;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.saptarshidebnath.lib.processrunner.exception.ProcessConfigurationException;
import com.saptarshidebnath.lib.processrunner.utilities.Constants;
import com.saptarshidebnath.lib.processrunner.utilities.fileutils.TempFile;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.Test;

public class ConfigurationBuilderTest {
  @Test(expected = ProcessConfigurationException.class)
  public void setMasterLogFileTest() throws ProcessConfigurationException {
    new ConfigurationBuilder("abc", "def")
        .setMasterLogFile(new File(Constants.USER_DIR), Boolean.FALSE);
  }

  @Test
  public void confgirationBuilderToString() throws IOException, ProcessConfigurationException {
    File tempFile = new TempFile().createTempLogDump();
    String toStringOfConfigBuilder =
        new ConfigurationBuilder("interpreter", "command")
            .setWorkigDir(new File(Constants.USER_DIR).toPath())
            .setParam("param1")
            .enableLogStreaming(Boolean.TRUE)
            .setMasterLogFile(tempFile, Boolean.TRUE, Charset.defaultCharset())
            .toString();

    String expectedToString =
        "ConfigurationBuilder{interpreter='interpreter', "
            + "command='command', "
            + "comamndParams=[param1], "
            + "workingDir="
            + new File(Constants.USER_DIR)
            + ", "
            + "masterLogFile="
            + tempFile.getCanonicalPath()
            + ", "
            + "autoDeleteFileOnExit=true, "
            + "logStreamingEnabled=true, "
            + "charset="
            + Charset.defaultCharset().toString()
            + "}";
    assertThat(
        "Configuration Builder to string validation",
        toStringOfConfigBuilder,
        is(expectedToString));
  }
}
