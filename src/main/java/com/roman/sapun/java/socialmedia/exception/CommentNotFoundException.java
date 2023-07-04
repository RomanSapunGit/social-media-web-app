package com.roman.sapun.java.socialmedia.exception;

public class CommentNotFoundException extends Exception{
    private static final String DEFAULT_MESSAGE = "Comment not found";

    public CommentNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
