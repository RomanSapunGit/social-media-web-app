package com.roman.sapun.java.socialmedia.dto;

import com.roman.sapun.java.socialmedia.entity.UserEntity;

public record VoteDTO(String name, String username, FileDTO image) {
    public VoteDTO(UserEntity user, FileDTO image) {
        this(user.getName(), user.getUsername(), image);
    }
}
