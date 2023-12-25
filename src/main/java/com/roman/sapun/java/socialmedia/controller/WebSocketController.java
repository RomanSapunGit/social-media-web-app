package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.comment.JsonFriendlyCommentDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {
    @MessageMapping("/ws")
    @SendTo("/topic/comments")
    public JsonFriendlyCommentDTO sendComment(JsonFriendlyCommentDTO comment) {
        return comment;
    }
}
