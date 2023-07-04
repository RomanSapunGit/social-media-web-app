package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.RequestCommentDTO;
import com.roman.sapun.java.socialmedia.dto.ResponseCommentDTO;
import com.roman.sapun.java.socialmedia.exception.CommentNotFoundException;
import com.roman.sapun.java.socialmedia.exception.PostNotFoundException;
import org.springframework.security.core.Authentication;

import java.util.List;


public interface CommentService {

      ResponseCommentDTO createComment(RequestCommentDTO requestCommentDTO, Authentication authentication) throws PostNotFoundException;

      ResponseCommentDTO deleteComment(String identifier, Authentication authentication) throws CommentNotFoundException;

    List<ResponseCommentDTO> getCommentsByPostIdentifier(String identifier);
}
