package com.roman.sapun.java.socialmedia.dto.post;

import com.roman.sapun.java.socialmedia.dto.FileDTO;
import com.roman.sapun.java.socialmedia.entity.PostEntity;

import java.sql.Timestamp;
import java.util.List;

public record ResponsePostDTO(String identifier, String title, String description, Timestamp creationTime, String username,
                              FileDTO userImage,
                              List<FileDTO> postImages) {
    public ResponsePostDTO(PostEntity postEntity, List<FileDTO> fileDTO, FileDTO userImage) {
        this(postEntity.getIdentifier(), postEntity.getTitle(), postEntity.getDescription(),
                postEntity.getCreationTime(), postEntity.getAuthor().getUsername(),userImage, fileDTO);
    }
}
