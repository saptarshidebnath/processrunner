package com.saptarshidebnath.processrunner.lib.jsonutils;

import com.saptarshidebnath.processrunner.lib.utilities.Utilities;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/** Created by saptarshi on 12/7/2016. */
public class UtilitiesTest {
  private final File tempFile;

  public UtilitiesTest() throws IOException {
    this.tempFile = Utilities.createTempLogDump();
  }

  @After
  public void tearDown() throws Exception {
    final boolean response = this.tempFile.delete();
    assertThat("Checking if temporary file is deletable or not ", response, is(true));
  }

  @Test
  public void createTempLogDump() throws Exception {
    final boolean response = this.tempFile.exists();
    assertThat("Checking if File exists ", response, is(true));
  }
}
