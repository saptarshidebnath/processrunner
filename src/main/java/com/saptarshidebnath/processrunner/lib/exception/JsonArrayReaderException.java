package com.saptarshidebnath.processrunner.lib.exception;

/** Basic exception denote that the {@link JsonArrayReaderException } is not configured correctly */
public class JsonArrayReaderException extends Exception {
  private final String message;
  /** Default constructor with default message chained to the other constructor */
  public JsonArrayReaderException() {
    this("Error in configuration");
  }

  /**
   * The constructor accepting a string message
   *
   * @param message
   */
  public JsonArrayReaderException(final String message) {
    super(message);
    this.message = message;
  }
}