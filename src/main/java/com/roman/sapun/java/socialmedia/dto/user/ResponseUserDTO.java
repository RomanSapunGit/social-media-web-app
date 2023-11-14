package com.roman.sapun.java.socialmedia.dto.user;

import com.roman.sapun.java.socialmedia.dto.FileDTO;
import com.roman.sapun.java.socialmedia.entity.UserEntity;

public record ResponseUserDTO(String name, String username, FileDTO userImage) {
    public ResponseUserDTO(UserEntity user, FileDTO userImage) {
        this(user.getName(), user.getUsername(), userImage);
    }
}
