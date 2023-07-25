package com.roman.sapun.java.socialmedia.dto.user;

import com.roman.sapun.java.socialmedia.dto.post.PostDTO;
import com.roman.sapun.java.socialmedia.entity.UserEntity;

import java.util.List;

public record UserDTO(String name, String username, List<PostDTO> posts) {
    public UserDTO(UserEntity user, List<PostDTO> posts) {
        this(user.getName(), user.getUsername(),
                posts);
    }
}
