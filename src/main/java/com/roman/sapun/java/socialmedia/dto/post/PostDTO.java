package com.roman.sapun.java.socialmedia.dto.post;

import com.roman.sapun.java.socialmedia.dto.FileDTO;
import com.roman.sapun.java.socialmedia.dto.comment.ResponseCommentDTO;
import com.roman.sapun.java.socialmedia.entity.PostEntity;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public record PostDTO(String identifier, String title, String description, Timestamp creationTime, String username,
                      FileDTO userImage,
                      List<FileDTO> postImages, Map<String, Object> commentsPage) {
    public PostDTO(PostEntity postEntity, List<FileDTO> fileDTO, FileDTO userImage, Map<String, Object> commentsPage) {
        this(postEntity.getIdentifier(), postEntity.getTitle(), postEntity.getDescription(),
                postEntity.getCreationTime(), postEntity.getAuthor().getUsername(), userImage, fileDTO, commentsPage);
    }
}
