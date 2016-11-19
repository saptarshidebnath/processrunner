package name.saptarshidebnath.processrunner.lib.exception;

import name.saptarshidebnath.processrunner.lib.ProcessConfiguration;

/** Basic exception denote that the {@link ProcessConfiguration} is not configured correctly */
public class ProcessConfigurationException extends Exception {
  private final String message;
  /** Default constructor with default message chained to the other constructor */
  public ProcessConfigurationException() {
    this("Error in process configuration");
  }

  /**
   * The constructor accepting a string message
   *
   * @param message
   */
  public ProcessConfigurationException(final String message) {
    super(message);
    this.message = message;
  }
}
