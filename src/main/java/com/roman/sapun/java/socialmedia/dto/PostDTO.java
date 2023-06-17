package com.roman.sapun.java.socialmedia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public record PostDTO(String title, String description, @JsonProperty("Creation Date") Timestamp creationDate) {
    public PostDTO(RequestPostDTO post,  Timestamp creationDate) {
        this(post.title(), post.description(), creationDate);
    }
}
