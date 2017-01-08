package com.saptarshidebnath.processrunner.lib.exception;

import com.saptarshidebnath.processrunner.lib.process.ProcessConfiguration;

/**
 * Custom {@link Exception} denote that the {@link ProcessConfiguration} is not configured correctly
 */
public class ProcessConfigurationException extends Exception {

  /**
   * The constructor accepting a string message
   *
   * @param message The message that needs to be provided while creating the exception.
   */
  public ProcessConfigurationException(final String message) {
    super(message);
  }
}
