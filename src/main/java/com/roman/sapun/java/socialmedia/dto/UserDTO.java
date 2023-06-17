package com.roman.sapun.java.socialmedia.dto;

import com.roman.sapun.java.socialmedia.entity.UserEntity;


public record UserDTO( String name,  String username,  String email) {
    public UserDTO(UserEntity user) {
      this(user.getName(), user.getUsername(), user.getEmail());
    }
}
