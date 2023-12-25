package com.roman.sapun.java.socialmedia.dto.comment;

import com.roman.sapun.java.socialmedia.dto.JsonFriendlyFileDTO;

import java.sql.Timestamp;

public record JsonFriendlyCommentDTO(String identifier, String title, String description, String username,
                                     Timestamp creationTime, JsonFriendlyFileDTO userImage) {
}
