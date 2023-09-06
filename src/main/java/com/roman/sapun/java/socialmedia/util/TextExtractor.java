package com.roman.sapun.java.socialmedia.util;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface TextExtractor {
    String extractUsernameFromJson(String jsonString) throws JsonProcessingException;
}
