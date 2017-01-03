package com.saptarshidebnath.processrunner.lib.exception;

import com.saptarshidebnath.processrunner.lib.utilities.Constants;

/**
 * Blanket exception to be used by the {@link
 * com.saptarshidebnath.processrunner.lib.process.ProcessRunnerFactory} class. The Developer should
 * be able to get the cause from the {@link ProcessException} detailing exactly what triggered the
 * exception.
 */
public class ProcessException extends Exception {
  public ProcessException(final Throwable throwable) {
    super(throwable);
  }

  public ProcessException() {
    this(new Exception(Constants.GENERIC_ERROR));
  }
}
