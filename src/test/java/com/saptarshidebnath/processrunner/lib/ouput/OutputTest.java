package com.saptarshidebnath.processrunner.lib.ouput;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.saptarshidebnath.processrunner.lib.output.OutputSourceType;
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
}
