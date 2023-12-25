package com.roman.sapun.java.socialmedia.dto.websocket;

import java.sql.Timestamp;

public record WebSocketCommentDTO(String identifier, String title, String description, String username,
                                  Timestamp creationTime, WebSocketFileDTO userImage) {
}
