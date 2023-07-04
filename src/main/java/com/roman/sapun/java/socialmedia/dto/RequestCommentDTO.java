package com.roman.sapun.java.socialmedia.dto;

import org.springframework.lang.NonNull;

public record RequestCommentDTO(String postIdentifier, @NonNull String title, @NonNull String description) {
}
