package com.roman.sapun.java.socialmedia.exception;

public class DownvotesNotFoundException extends Exception {
    private static final String DEFAULT_MESSAGE = "Downvotes not found";

    public DownvotesNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

}
