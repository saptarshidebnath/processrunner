package com.saptarshidebnath.processrunner.lib.exception;

/**
 * Custom exception denote that the {@link
 * com.saptarshidebnath.processrunner.lib.jsonutils.WriteJsonArrayToFile } is not configured
 * correctly.
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
