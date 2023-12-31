package com.roman.sapun.java.socialmedia.dto.websocket;


import java.sql.Timestamp;
import java.util.List;

public record WebSocketPostDTO (
        String identifier, String title, String description, Timestamp creationTime,
        String username,
        WebSocketFileDTO userImage,
        List<WebSocketFileDTO> postImages) {
}
