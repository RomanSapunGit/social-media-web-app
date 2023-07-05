package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.RequestCommentDTO;
import com.roman.sapun.java.socialmedia.dto.ResponseCommentDTO;
import com.roman.sapun.java.socialmedia.exception.CommentNotFoundException;
import com.roman.sapun.java.socialmedia.exception.PostNotFoundException;
import com.roman.sapun.java.socialmedia.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/comment")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseCommentDTO createComment(@RequestBody RequestCommentDTO requestCommentDTO, Authentication authentication) throws PostNotFoundException {
        return commentService.createComment(requestCommentDTO, authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public ResponseCommentDTO deleteComment(@PathVariable String id, Authentication authentication) throws CommentNotFoundException {
        return commentService.deleteComment(id, authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public Map<String, Object> getCommentsByPostIdentifier(@PathVariable String id, @RequestParam int page) {
        return commentService.getCommentsByPostIdentifier(id, page);
    }
}
