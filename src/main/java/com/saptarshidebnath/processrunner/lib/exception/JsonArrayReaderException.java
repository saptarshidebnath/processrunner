package com.saptarshidebnath.processrunner.lib.exception;

/**
 * Custom exception denote that the {@link
 * com.saptarshidebnath.processrunner.lib.jsonutils.ReadJsonArrayFromFile } is not configured
 * correctly.
 */
public class JsonArrayReaderException extends Exception {

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
