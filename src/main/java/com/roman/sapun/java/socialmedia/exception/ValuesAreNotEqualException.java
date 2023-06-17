package com.roman.sapun.java.socialmedia.exception;

public class ValuesAreNotEqualException extends Exception {
    private static final String DEFAULT_MESSAGE = "Values are not equal";
    public ValuesAreNotEqualException() {
        super(DEFAULT_MESSAGE);
    }

}
