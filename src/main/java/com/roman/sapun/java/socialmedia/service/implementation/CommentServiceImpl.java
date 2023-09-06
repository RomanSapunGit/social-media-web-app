package com.roman.sapun.java.socialmedia.service.implementation;

import com.roman.sapun.java.socialmedia.config.ValueConfig;
import com.roman.sapun.java.socialmedia.dto.comment.CommentDTO;
import com.roman.sapun.java.socialmedia.dto.comment.RequestCommentDTO;
import com.roman.sapun.java.socialmedia.dto.comment.ResponseCommentDTO;
import com.roman.sapun.java.socialmedia.entity.CommentEntity;
import com.roman.sapun.java.socialmedia.exception.CommentNotFoundException;
import com.roman.sapun.java.socialmedia.repository.CommentRepository;
import com.roman.sapun.java.socialmedia.repository.PostRepository;
import com.roman.sapun.java.socialmedia.service.CommentService;
import com.roman.sapun.java.socialmedia.service.ImageService;
import com.roman.sapun.java.socialmedia.service.UserService;
import com.roman.sapun.java.socialmedia.util.converter.CommentConverter;
import com.roman.sapun.java.socialmedia.util.converter.PageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentConverter commentConverter;
    private final UserService userService;
    private final PostRepository postRepository;
    private final PageConverter pageConverter;
    private final ValueConfig valueConfig;
    private final ImageService imageService;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, CommentConverter commentConverter,
                              UserService userService, PostRepository postRepository, PageConverter pageConverter,
                              ValueConfig valueConfig, ImageService imageService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.commentConverter = commentConverter;
        this.postRepository = postRepository;
        this.pageConverter = pageConverter;
        this.valueConfig = valueConfig;
        this.imageService = imageService;
    }

    @Override
    public CommentDTO createComment(RequestCommentDTO requestCommentDTO, Authentication authentication) {
        var commentOwner = userService.findUserByAuth(authentication);
        var postEntity = postRepository.findByIdentifier(requestCommentDTO.postIdentifier());
        var commentEntity = commentConverter.convertToCommentEntity(requestCommentDTO, new CommentEntity(), commentOwner, postEntity);
        commentRepository.save(commentEntity);
        return new CommentDTO(commentEntity, imageService.getImageByUser(commentEntity.getAuthor().getUsername()));
    }

    @Override
    public Map<String, Object> getCommentsByPostIdentifier(String identifier, int pageNumber) {
        var postEntity = postRepository.findByIdentifier(identifier);
        var pageable = PageRequest.of(pageNumber, valueConfig.getPageSize() - 47);
        var commentPage = commentRepository.findCommentEntitiesByPost(postEntity, pageable);
        return pageConverter.convertPageToResponse(commentPage.map(comment ->
                new ResponseCommentDTO(comment, imageService.getImageByUser(comment.getAuthor().getUsername()))));
    }

    @Override
    public ResponseCommentDTO deleteComment(String identifier, Authentication authentication) throws CommentNotFoundException {
        var commentOwner = userService.findUserByAuth(authentication);
        var commentEntity = commentRepository.findByIdentifier(identifier);
        if (!commentEntity.getAuthor().equals(commentOwner)) {
            throw new CommentNotFoundException();
        }
        commentRepository.delete(commentEntity);
        return new ResponseCommentDTO(commentEntity, imageService.getImageByUser(commentEntity.getAuthor().getUsername()));
    }

    @Override
    public ResponseCommentDTO updateCommentById(RequestCommentDTO requestCommentDTO, String identifier, Authentication authentication) throws CommentNotFoundException {
        var commentOwner = userService.findUserByAuth(authentication);
        var commentEntity = commentRepository.findByIdentifier(identifier);
        if (!commentEntity.getAuthor().equals(commentOwner)) {
            throw new CommentNotFoundException();
        }
        commentEntity.setTitle(requestCommentDTO.title() == null ? commentEntity.getTitle() : requestCommentDTO.title());

        commentEntity.setDescription(requestCommentDTO.description() == null ?
                commentEntity.getDescription() : requestCommentDTO.description());
        commentRepository.save(commentEntity);
        return new ResponseCommentDTO(commentEntity, imageService.getImageByUser(commentEntity.getAuthor().getUsername()));
    }
}
