package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.websocket.WebSocketCommentDTO;
import com.roman.sapun.java.socialmedia.dto.websocket.WebSocketPostDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
/**
 * WebSocketController is a Spring WebSockets controller responsible for handling real-time communication
 * related to comments and posts in the application. It utilizes the @MessageMapping annotation to map
 * incoming WebSocket messages to specific methods and the @SendTo annotation to specify the destination
 * for sending WebSocket responses.
 * <p>
 * Methods:
 * <p>
 * 1. sendComment:
 *    - Endpoint: "/comment/create/{postId}"
 *    - Description: Handles the creation of a new comment for a given post.
 *    - Input: WebSocketCommentDTO object representing the new comment.
 *    - Output: Sends the WebSocketCommentDTO to the "/topic/comments/create/{postId}" destination.
 * <p>
 * 2. sendUpdatedComment:
 *    - Endpoint: "/comment/update/{postId}"
 *    - Description: Handles the update of an existing comment for a given post.
 *    - Input: WebSocketCommentDTO object representing the updated comment.
 *    - Output: Sends the WebSocketCommentDTO to the "/topic/comments/update/{postId}" destination.
 * <p>
 * 3. sendUpdatedPost:
 *    - Endpoint: "/post/update/{postId}"
 *    - Description: Handles the update of an existing post.
 *    - Input: WebSocketPostDTO object representing the updated post.
 *    - Output: Sends the WebSocketPostDTO to the "/topic/posts/update/{postId}" destination.
 * <p>
 * Note:
 * - @MessageMapping is used to map the incoming WebSocket messages to specific methods.
 * - @SendTo is used to specify the destination for sending the WebSocket response.
 * - The paths include placeholders like "{postId}" to dynamically identify the associated post.
 * - WebSocketCommentDTO and WebSocketPostDTO are data transfer objects representing comment and post data.
 * - This controller enables real-time communication for comment and post updates in the application.
 */
@RestController
public class WebSocketController {
    @MessageMapping("/comment/create/{postId}")
    @SendTo("/topic/comments/create/{postId}")
    public WebSocketCommentDTO sendComment(WebSocketCommentDTO comment) {
        return comment;
    }

    @MessageMapping("/comment/update/{postId}")
    @SendTo("/topic/comments/update/{postId}")
    public WebSocketCommentDTO sendUpdatedComment(WebSocketCommentDTO comment) {
        return comment;
    }

    @MessageMapping("/post/update/{postId}")
    @SendTo("/topic/posts/update/{postId}")
    public WebSocketPostDTO sendUpdatedPost(WebSocketPostDTO post) {
        return post;
    }
}
