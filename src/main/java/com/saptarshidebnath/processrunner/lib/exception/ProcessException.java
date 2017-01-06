package com.saptarshidebnath.processrunner.lib.exception;

import com.saptarshidebnath.processrunner.lib.utilities.Constants;

/**
 * Blanket exception to be used by the {@link
 * com.saptarshidebnath.processrunner.lib.process.ProcessRunnerFactory} class and all public facing
 * methods for this ProcessRunner library. The Developer should be able to get the cause from the
 * {@link ProcessException#getCause()} detailing exactly what triggered the exception.
 */
public class ProcessException extends Exception {
  /**
   * Creates a {@link ProcessException} from another {@link Exception}
   *
   * @param ex Accepts a {@link Exception} object as input.
   */
  public ProcessException(final Exception ex) {
    super(ex);
  }

  /**
   * Creates a generic {@link ProcessException} with the message {@link Constants#GENERIC_ERROR}.
   */
  public ProcessException() {
    this(new Exception(Constants.GENERIC_ERROR));
  }
}
