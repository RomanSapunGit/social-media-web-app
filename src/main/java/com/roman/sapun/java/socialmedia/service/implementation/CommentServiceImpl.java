package com.roman.sapun.java.socialmedia.service.implementation;

import com.roman.sapun.java.socialmedia.config.ValueConfig;
import com.roman.sapun.java.socialmedia.dto.comment.CommentDTO;
import com.roman.sapun.java.socialmedia.dto.comment.RequestCommentDTO;
import com.roman.sapun.java.socialmedia.dto.comment.ResponseCommentDTO;
import com.roman.sapun.java.socialmedia.dto.page.CommentPageDTO;
import com.roman.sapun.java.socialmedia.entity.CommentEntity;
import com.roman.sapun.java.socialmedia.exception.CommentNotFoundException;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import com.roman.sapun.java.socialmedia.exception.UserStatisticsNotFoundException;
import com.roman.sapun.java.socialmedia.repository.CommentRepository;
import com.roman.sapun.java.socialmedia.repository.PostRepository;
import com.roman.sapun.java.socialmedia.repository.UserRepository;
import com.roman.sapun.java.socialmedia.service.CommentService;
import com.roman.sapun.java.socialmedia.service.UserService;
import com.roman.sapun.java.socialmedia.service.UserStatisticsService;
import com.roman.sapun.java.socialmedia.util.converter.CommentConverter;
import com.roman.sapun.java.socialmedia.util.converter.ImageConverter;
import com.roman.sapun.java.socialmedia.util.converter.PageConverter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentConverter commentConverter;
    private final UserService userService;
    private final PostRepository postRepository;
    private final PageConverter pageConverter;
    private final ValueConfig valueConfig;
    private final UserRepository userRepository;
    private final UserStatisticsService userStatisticsService;
    private final ImageConverter imageConverter;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, CommentConverter commentConverter,
                              UserService userService, PostRepository postRepository, PageConverter pageConverter,
                              ValueConfig valueConfig, UserRepository userRepository,
                              UserStatisticsService userStatisticsService, ImageConverter imageConverter) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.commentConverter = commentConverter;
        this.postRepository = postRepository;
        this.pageConverter = pageConverter;
        this.valueConfig = valueConfig;
        this.userRepository = userRepository;
        this.userStatisticsService = userStatisticsService;
        this.imageConverter = imageConverter;
    }

    @Override
    public CommentDTO createComment(RequestCommentDTO requestCommentDTO, Authentication authentication, HttpServletRequest request) throws UserStatisticsNotFoundException, CommentNotFoundException, UserNotFoundException {
        var commentOwner = userService.findUserByAuth(authentication);
        var postEntity = postRepository.findByIdentifier(requestCommentDTO.postIdentifier()).orElseThrow(CommentNotFoundException::new);
        var commentEntity = commentConverter.convertToCommentEntity(requestCommentDTO, new CommentEntity(), commentOwner, postEntity);
        commentEntity.setUserStatistics(commentOwner.getUserStatistics());
        commentRepository.save(commentEntity);
        var consent = commentOwner.getUserStatistics().getConsent() == null ? "false" : commentOwner.getUserStatistics().getConsent();
        if(consent.equals("true")) {
            userStatisticsService.addCreatedCommentToStatistic(commentOwner, commentEntity, request);
        }
        return new CommentDTO(commentEntity, imageConverter.convertImageToDTO(commentEntity.getAuthor().getImage()));
    }

    @Override
    public CommentPageDTO getCommentsByPostIdentifier(String identifier, int pageNumber) throws CommentNotFoundException {
        var postEntity = postRepository.findByIdentifier(identifier).orElseThrow(CommentNotFoundException::new);
        var pageable = PageRequest.of(pageNumber, valueConfig.getPageSize() - 30);
        var commentPage = commentRepository.findCommentEntitiesByPost(postEntity, pageable);
        return pageConverter.convertPageToCommentPageDTO(commentPage);
    }

    @Override
    public ResponseCommentDTO deleteComment(String identifier, Authentication authentication) throws CommentNotFoundException, UserNotFoundException {
        var commentOwner = userService.findUserByAuth(authentication);
        var commentEntity = commentRepository.findByIdentifier(identifier).orElseThrow(CommentNotFoundException::new);
        if (!commentEntity.getAuthor().equals(commentOwner)) {
            throw new CommentNotFoundException();
        }
        commentRepository.delete(commentEntity);
        return new ResponseCommentDTO(commentEntity, imageConverter.convertImageToDTO(commentEntity.getAuthor().getImage()));
    }

    @Override
    public ResponseCommentDTO updateCommentById(RequestCommentDTO requestCommentDTO, String identifier, Authentication authentication) throws CommentNotFoundException, UserNotFoundException {
        var commentOwner = userService.findUserByAuth(authentication);
        var commentEntity = commentRepository.findByIdentifier(identifier).orElseThrow(CommentNotFoundException::new);
        if (!commentEntity.getAuthor().equals(commentOwner)) {
            throw new CommentNotFoundException();
        }
        commentEntity.setTitle(requestCommentDTO.title() == null ? commentEntity.getTitle() : requestCommentDTO.title());

        commentEntity.setDescription(requestCommentDTO.description() == null ?
                commentEntity.getDescription() : requestCommentDTO.description());
        commentRepository.save(commentEntity);
        return new ResponseCommentDTO(commentEntity, imageConverter.convertImageToDTO(commentEntity.getAuthor().getImage()));
    }

    @Override
    public CommentPageDTO getSavedComments(Authentication authentication, int pageNumber) throws UserNotFoundException {
        var user = userService.findUserByAuth(authentication);
        var savedPosts = user.getSavedComments();

        List<CommentEntity> savedPostsList = new ArrayList<>(savedPosts);

        Pageable pageable = PageRequest.of(pageNumber, valueConfig.getPageSize());

        Page<CommentEntity> savedPostsPage = new PageImpl<>(savedPostsList, pageable, savedPostsList.size());

        return pageConverter.convertPageToCommentPageDTO(savedPostsPage);
    }

    @Override
    public boolean findSavedCommentByIdentifier(String identifier, Authentication authentication) throws UserNotFoundException {
        var user = userService.findUserByAuth(authentication);
        return user.getSavedComments().stream().anyMatch(comment -> comment.getIdentifier().equals(identifier));
    }

    @Override
    public ResponseCommentDTO removeCommentFromSavedList(String identifier, Authentication authentication) throws UserNotFoundException, CommentNotFoundException {
        var user = userService.findUserByAuth(authentication);
        var commentEntity = commentRepository.findByIdentifier(identifier).orElseThrow(CommentNotFoundException::new);
        user.getSavedComments().remove(commentEntity);
        userRepository.save(user);
        return new ResponseCommentDTO(commentEntity, imageConverter.convertImageToDTO(commentEntity.getAuthor().getImage()));
    }

    @Override
    public ResponseCommentDTO addCommentToSavedList(String identifier, Authentication authentication) throws UserNotFoundException, CommentNotFoundException {
        var user = userService.findUserByAuth(authentication);
        var commentEntity = commentRepository.findByIdentifier(identifier).orElseThrow(CommentNotFoundException::new);
        user.getSavedComments().add(commentEntity);
        userRepository.save(user);
        return new ResponseCommentDTO(commentEntity, imageConverter.convertImageToDTO(commentEntity.getAuthor().getImage()));
    }
}
