package com.saptarshidebnath.processrunner.lib.exception;

/**
 * Basic exception denote that the {@link JsonArrayWriterException } is not configured correctly.
 */
public class JsonArrayWriterException extends Exception {

  /**
   * The constructor accepting a string message.
   *
   * @param message The message that needs to be provided while creating the exception.
   */
  public JsonArrayWriterException(final String message) {
    super(message);
  }
}
