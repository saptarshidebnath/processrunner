package com.saptarshidebnath.processrunner.lib.exception;

/**
 * Basic exception denote that the {@link JsonArrayReaderException } is not configured correctly.
 */
public class JsonArrayReaderException extends Exception {

  /** Default constructor with default message chained to the other constructor. */
  public JsonArrayReaderException() {
    this("Error in configuration");
  }

  /**
   * The constructor accepting a string message.
   *
   * @param message Takes a String as input
   */
  public JsonArrayReaderException(final String message) {
    this(new Exception(message));
  }

  /**
   * Create a {@link JsonArrayReaderException} with already existing exception.
   *
   * @param exception
   */
  public JsonArrayReaderException(final Exception exception) {
    super(exception);
  }
}
