package com.roman.sapun.java.socialmedia.exception;

public class InvalidValueException extends Exception {
    private static final String DEFAULT_MESSAGE = "Invalid value";

    public InvalidValueException(String message) {
        super(message);
    }
}
