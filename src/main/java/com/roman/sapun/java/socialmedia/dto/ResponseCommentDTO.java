package com.roman.sapun.java.socialmedia.dto;

import com.roman.sapun.java.socialmedia.entity.CommentEntity;
import org.springframework.lang.NonNull;

public record ResponseCommentDTO(@NonNull String identifier, @NonNull String title, @NonNull String description) {
    public ResponseCommentDTO(CommentEntity requestCommentDTO) {
        this(requestCommentDTO.getIdentifier(), requestCommentDTO.getTitle(), requestCommentDTO.getDescription()    );
    }
}
