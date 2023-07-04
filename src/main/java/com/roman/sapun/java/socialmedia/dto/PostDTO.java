package com.roman.sapun.java.socialmedia.dto;

import com.roman.sapun.java.socialmedia.entity.PostEntity;

import java.sql.Timestamp;

public record PostDTO(String identifier, String title, String description, Timestamp creationDate, String username) {
    public PostDTO(PostEntity postEntity) {
        this(postEntity.getIdentifier(), postEntity.getTitle(), postEntity.getDescription(),
                postEntity.getCreationTime(), postEntity.getAuthor().getUsername());
    }
}
