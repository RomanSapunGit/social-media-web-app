package com.roman.sapun.java.socialmedia.exception;

public class TokenExpiredException extends Exception {
    private static final String DEFAULT_MESSAGE = "Token expired";
    public TokenExpiredException() {
        super(DEFAULT_MESSAGE);
    }
}
