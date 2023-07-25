package com.roman.sapun.java.socialmedia.dto.comment;

import org.springframework.lang.NonNull;

public record RequestCommentDTO(String postIdentifier,  String title,  String description) {
}
