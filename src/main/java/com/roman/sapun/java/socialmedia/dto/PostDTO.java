package com.roman.sapun.java.socialmedia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roman.sapun.java.socialmedia.entity.PostEntity;

import java.sql.Timestamp;

public record PostDTO(String title, String description, @JsonProperty("Creation Date") Timestamp creationDate) {
    public PostDTO(RequestPostDTO post, Timestamp creationDate) {
        this(post.title(), post.description(), creationDate);
    }

    public PostDTO(PostEntity postEntity) {
        this(postEntity.getTitle(), postEntity.getDescription(), postEntity.getCreationTime());
    }
}
