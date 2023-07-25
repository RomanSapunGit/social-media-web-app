package com.roman.sapun.java.socialmedia.dto.user;

import com.roman.sapun.java.socialmedia.entity.UserEntity;

public record ResponseUserDTO(String name, String username) {
    public ResponseUserDTO(UserEntity user) {
        this(user.getName(), user.getUsername());
    }
}
