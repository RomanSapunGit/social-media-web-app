package com.roman.sapun.java.socialmedia.dto.post;

import com.roman.sapun.java.socialmedia.dto.FileDTO;
import com.roman.sapun.java.socialmedia.dto.image.ResponseImageDTO;
import com.roman.sapun.java.socialmedia.dto.user.UserDTO;
import com.roman.sapun.java.socialmedia.entity.PostEntity;

import java.sql.Timestamp;
import java.util.List;

public record ResponsePostDTO(String identifier, String title, String description, Timestamp creationTime,
                              String username,
                              FileDTO userImage,
                              List<ResponseImageDTO> postImages,
                              List<UserDTO> upvotes,
                              List<UserDTO> downvotes) {
    public ResponsePostDTO(PostEntity postEntity, List<ResponseImageDTO> imageDTOS, FileDTO userImage, List<UserDTO> upvotes,
                           List<UserDTO> downvotes) {
        this(postEntity.getIdentifier(), postEntity.getTitle(), postEntity.getDescription(),
                postEntity.getCreationTime(), postEntity.getAuthor().getUsername(), userImage, imageDTOS, upvotes, downvotes);
    }
}
