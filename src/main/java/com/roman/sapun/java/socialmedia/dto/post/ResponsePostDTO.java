package com.roman.sapun.java.socialmedia.dto.post;

import com.roman.sapun.java.socialmedia.dto.FileDTO;
import com.roman.sapun.java.socialmedia.dto.user.ResponseUserDTO;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public record ResponsePostDTO(String identifier, String title, String description, Timestamp creationTime,
                              String username,
                              FileDTO userImage,
                              List<FileDTO> postImages, Set<ResponseUserDTO> upvotes, Set<ResponseUserDTO> downvotes) {
    public ResponsePostDTO(PostEntity postEntity, List<FileDTO> fileDTO, FileDTO userImage, Set<ResponseUserDTO> upvotes,
                           Set<ResponseUserDTO> downvotes) {
        this(postEntity.getIdentifier(), postEntity.getTitle(), postEntity.getDescription(),
                postEntity.getCreationTime(), postEntity.getAuthor().getUsername(), userImage, fileDTO, upvotes, downvotes);
    }
}
