package com.roman.sapun.java.socialmedia.dto.user;

import com.roman.sapun.java.socialmedia.entity.UserEntity;

public record UserViewDTO(String name, String username) {
    public UserViewDTO(UserEntity user) {
        this(user.getName(), user.getUsername());
    }
}
