package com.roman.sapun.java.socialmedia.dto.user;

import com.roman.sapun.java.socialmedia.entity.UserEntity;


public record RequestUserDTO(String name, String username, String email) {
    public RequestUserDTO(UserEntity user) {
      this(user.getName(), user.getUsername(), user.getEmail());
    }
}
