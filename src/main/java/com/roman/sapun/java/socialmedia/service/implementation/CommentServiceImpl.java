package com.roman.sapun.java.socialmedia.service.implementation;

import com.roman.sapun.java.socialmedia.dto.RequestCommentDTO;
import com.roman.sapun.java.socialmedia.dto.ResponseCommentDTO;
import com.roman.sapun.java.socialmedia.entity.CommentEntity;
import com.roman.sapun.java.socialmedia.exception.CommentNotFoundException;
import com.roman.sapun.java.socialmedia.repository.CommentRepository;
import com.roman.sapun.java.socialmedia.repository.PostRepository;
import com.roman.sapun.java.socialmedia.service.CommentService;
import com.roman.sapun.java.socialmedia.service.UserService;
import com.roman.sapun.java.socialmedia.util.CommentConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentConverter commentConverter;
    private final UserService userService;
    private final PostRepository postRepository;


    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, CommentConverter commentConverter,
                              UserService userService, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.commentConverter = commentConverter;
        this.postRepository = postRepository;
    }

    @Override
    public ResponseCommentDTO createComment(RequestCommentDTO requestCommentDTO, Authentication authentication) {
        var commentOwner = userService.findUserByAuth(authentication);
        var postEntity = postRepository.findByIdentifier(requestCommentDTO.postIdentifier());
        var commentEntity = commentConverter.convertToCommentEntity(requestCommentDTO, new CommentEntity(), commentOwner, postEntity);
        commentRepository.save(commentEntity);
        return new ResponseCommentDTO(commentEntity);
    }

    @Override
    public List<ResponseCommentDTO> getCommentsByPostIdentifier(String identifier) {
        var postEntity = postRepository.findByIdentifier(identifier);
        return postEntity.getComments().stream()
                .map(ResponseCommentDTO::new)
                .collect(Collectors.toList());
    }
    @Override
    public ResponseCommentDTO deleteComment(String identifier, Authentication authentication) throws CommentNotFoundException {
        var commentOwner = userService.findUserByAuth(authentication);
        var commentEntity = commentRepository.findByIdentifier(identifier);
        if (!commentEntity.getAuthor().equals(commentOwner)) {
            throw new CommentNotFoundException();
        }
        commentRepository.delete(commentEntity);
        return new ResponseCommentDTO(commentEntity);
    }
}
