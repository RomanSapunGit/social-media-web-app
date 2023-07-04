package com.roman.sapun.java.socialmedia.exception;

public class PostNotFoundException extends Exception {
    private static final String DEFAULT_MESSAGE = "Post not found";

    public PostNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
