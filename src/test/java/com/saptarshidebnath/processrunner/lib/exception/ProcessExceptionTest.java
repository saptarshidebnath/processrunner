package com.saptarshidebnath.processrunner.lib.exception;

import com.saptarshidebnath.processrunner.lib.utilities.Constants;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/** Created by saptarshi on 1/2/2017. */
public class ProcessExceptionTest {

  @Test
  public void processException() {
    try {
      throw new ProcessException();
    } catch (final ProcessException ex) {
      final String message = ex.getMessage();
      assertThat(
          "Validating message of default ProcessException : ",
          message,
          is("java.lang.Exception: " + Constants.GENERIC_ERROR));
    }
  }
}
