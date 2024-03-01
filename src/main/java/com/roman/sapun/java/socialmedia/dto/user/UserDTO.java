package com.roman.sapun.java.socialmedia.dto.user;

import com.roman.sapun.java.socialmedia.dto.FileDTO;
import com.roman.sapun.java.socialmedia.entity.UserEntity;

public record UserDTO(String name, String username, FileDTO image) {
    public UserDTO(UserEntity user, FileDTO image) {
        this(user.getName(), user.getUsername(), image);
    }
}
