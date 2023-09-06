package com.roman.sapun.java.socialmedia.util.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roman.sapun.java.socialmedia.util.TextExtractor;
import org.springframework.stereotype.Component;

@Component
public class TextExtractorImpl implements TextExtractor {
    @Override
    public String extractUsernameFromJson(String jsonString) throws JsonProcessingException {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonString);
            return rootNode.path("username").asText();
    }
}
