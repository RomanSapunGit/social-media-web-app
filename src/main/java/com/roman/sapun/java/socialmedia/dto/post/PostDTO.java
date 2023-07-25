package com.roman.sapun.java.socialmedia.dto.post;

import com.roman.sapun.java.socialmedia.entity.PostEntity;

import java.sql.Timestamp;
import java.util.List;

public record PostDTO(String identifier, String title, String description, Timestamp creationTime, String username) {
    public PostDTO(PostEntity postEntity) {
        this(postEntity.getIdentifier(), postEntity.getTitle(), postEntity.getDescription(),
                postEntity.getCreationTime(), postEntity.getAuthor().getUsername());
    }
}
