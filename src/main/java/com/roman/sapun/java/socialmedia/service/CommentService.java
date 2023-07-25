package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.comment.RequestCommentDTO;
import com.roman.sapun.java.socialmedia.dto.comment.ResponseCommentDTO;
import com.roman.sapun.java.socialmedia.exception.CommentNotFoundException;
import com.roman.sapun.java.socialmedia.exception.PostNotFoundException;
import org.springframework.security.core.Authentication;

import java.util.Map;


public interface CommentService {

    ResponseCommentDTO createComment(RequestCommentDTO requestCommentDTO, Authentication authentication) throws PostNotFoundException;

    ResponseCommentDTO deleteComment(String identifier, Authentication authentication) throws CommentNotFoundException;

    Map<String, Object> getCommentsByPostIdentifier(String identifier, int pageNumber);

    ResponseCommentDTO updateCommentById(RequestCommentDTO requestCommentDTO, String identifier, Authentication authentication) throws CommentNotFoundException;
}
