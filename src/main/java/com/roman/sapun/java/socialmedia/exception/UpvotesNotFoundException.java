package com.roman.sapun.java.socialmedia.exception;

public class UpvotesNotFoundException extends Exception{
    private static final String DEFAULT_MESSAGE = "Upvotes not found";

    public UpvotesNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
