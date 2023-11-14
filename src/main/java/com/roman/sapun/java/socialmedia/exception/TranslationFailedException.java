package com.roman.sapun.java.socialmedia.exception;

public class TranslationFailedException extends Exception {
    private static final String DEFAULT_MESSAGE = "Translation failed";

    public TranslationFailedException() {
        super(DEFAULT_MESSAGE);
    }

}

