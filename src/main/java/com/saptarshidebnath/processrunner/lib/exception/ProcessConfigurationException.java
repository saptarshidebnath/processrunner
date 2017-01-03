package com.saptarshidebnath.processrunner.lib.exception;

import com.saptarshidebnath.processrunner.lib.process.ProcessConfiguration;

/** Basic exception denote that the {@link ProcessConfiguration} is not configured correctly */
public class ProcessConfigurationException extends Exception {

  /**
   * The constructor accepting a string message
   *
   * @param message
   */
  public ProcessConfigurationException(final String message) {
    super(message);
  }
}
