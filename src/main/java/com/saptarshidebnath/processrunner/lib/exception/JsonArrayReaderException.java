package com.saptarshidebnath.processrunner.lib.exception;

/**
 * Custom {@link Exception} denote that the {@link
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
   * {@link JsonArrayReaderException} wrapper for already existing exception.
   *
   * @param exception Consumes a object of {@link Exception}
   */
  public JsonArrayReaderException(final Exception exception) {
    super(exception);
  }
}
