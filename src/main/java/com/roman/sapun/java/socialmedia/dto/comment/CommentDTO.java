package com.roman.sapun.java.socialmedia.dto.comment;

import com.roman.sapun.java.socialmedia.dto.FileDTO;
import com.roman.sapun.java.socialmedia.entity.CommentEntity;

import java.sql.Timestamp;

public record CommentDTO(String identifier, String title, String description, String username, String postAuthorUsername,
                         Timestamp creationTime, FileDTO userImage) {
    public CommentDTO(CommentEntity commentEntity, FileDTO imageByUser) {
        this(commentEntity.getIdentifier(), commentEntity.getTitle(), commentEntity.getDescription(),
                commentEntity.getAuthor().getUsername(), commentEntity.getPost().getAuthor().getUsername(),
                commentEntity.getCreationTime(), imageByUser);
    }
}
