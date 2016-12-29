package com.saptarshidebnath.processrunner.lib.exception;

/**
 * Basic exception denote that the {@link JsonArrayWriterException } is not configured correctly.
 */
public class JsonArrayWriterException extends Exception {
    /**
     * Default constructor with default message chained to the other constructor.
     */
    public JsonArrayWriterException() {
        this("Error in process configuration");
    }

    /**
     * The constructor accepting a string message.
     *
     * @param message The message that needs to be provided while creating the exception.
     */
    public JsonArrayWriterException(final String message) {
        super(message);
    }
}
