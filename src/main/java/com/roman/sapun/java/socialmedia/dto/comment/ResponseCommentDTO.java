package com.roman.sapun.java.socialmedia.dto.comment;

import com.roman.sapun.java.socialmedia.dto.FileDTO;
import com.roman.sapun.java.socialmedia.entity.CommentEntity;

import java.sql.Timestamp;

public record ResponseCommentDTO(String identifier, String title, String description, String username,
                                 Timestamp creationTime, FileDTO userImage) {
    public ResponseCommentDTO(CommentEntity commentEntity, FileDTO image) {
        this(commentEntity.getIdentifier(), commentEntity.getTitle(), commentEntity.getDescription(),
                commentEntity.getAuthor().getUsername(), commentEntity.getCreationTime(), image);
    }
}
