package com.roman.sapun.java.socialmedia.exception;

public class TagNotFoundException extends   Exception{
    private static final String DEFAULT_MESSAGE = "Tag not found";
    public TagNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
