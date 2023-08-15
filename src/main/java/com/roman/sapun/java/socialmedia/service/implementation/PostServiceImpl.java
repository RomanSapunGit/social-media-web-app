package com.roman.sapun.java.socialmedia.service.implementation;

import com.roman.sapun.java.socialmedia.config.ValueConfig;
import com.roman.sapun.java.socialmedia.dto.comment.ResponseCommentDTO;
import com.roman.sapun.java.socialmedia.dto.post.PostDTO;
import com.roman.sapun.java.socialmedia.dto.post.RequestPostDTO;
import com.roman.sapun.java.socialmedia.dto.post.ResponsePostDTO;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.TagEntity;
import com.roman.sapun.java.socialmedia.exception.PostNotFoundException;
import com.roman.sapun.java.socialmedia.repository.PostRepository;
import com.roman.sapun.java.socialmedia.repository.TagRepository;
import com.roman.sapun.java.socialmedia.repository.UserRepository;
import com.roman.sapun.java.socialmedia.service.*;
import com.roman.sapun.java.socialmedia.util.converter.CommentConverter;
import com.roman.sapun.java.socialmedia.util.converter.PageConverter;
import com.roman.sapun.java.socialmedia.util.converter.PostConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PostServiceImpl implements PostService {

    private final TagService tagService;
    private final UserService userService;
    private final PostConverter postConverter;
    private final PostRepository postRepository;
    private final PageConverter pageConverter;
    private final ValueConfig valueConfig;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final CommentService commentService;
    private final CommentConverter commentConverter;
    private static final int TWELVE_HOURS = 86400;


    public PostServiceImpl(TagService tagService, PostConverter postConverter, UserService userService,
                           PostRepository postRepository, PageConverter pageConverter, ValueConfig valueConfig,
                           TagRepository tagRepository, UserRepository userRepository, ImageService imageService,
                           CommentService commentService, CommentConverter commentConverter) {
        this.tagService = tagService;
        this.postConverter = postConverter;
        this.userService = userService;
        this.postRepository = postRepository;
        this.pageConverter = pageConverter;
        this.valueConfig = valueConfig;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.imageService = imageService;
        this.commentService = commentService;
        this.commentConverter = commentConverter;
    }

    @Override
    public ResponsePostDTO createPost(RequestPostDTO requestPostDTO, List<MultipartFile> images, Authentication authentication) {
        Set<TagEntity> existingTags = tagService.getExistingTagsFromText(requestPostDTO.description());
        Set<TagEntity> nonExistingTags = tagService.saveNonExistingTagsFromText(requestPostDTO.description());
        existingTags.addAll(nonExistingTags);
        var postOwner = userService.findUserByAuth(authentication);
        var postEntity = postConverter.convertToPostEntity(requestPostDTO, existingTags, postOwner, new PostEntity());
        postRepository.save(postEntity);
        if (images != null) {
            var DTOImages = imageService.uploadImagesForPost(images, postEntity.getIdentifier(), authentication);
            return new ResponsePostDTO(postEntity, DTOImages, imageService.getImageByUser(postEntity.getAuthor().getUsername()));
        }
        return new ResponsePostDTO(postEntity, new ArrayList<>(), imageService.getImageByUser(postEntity.getAuthor().getUsername()));
    }

    @Override
    public ResponsePostDTO updatePost(RequestPostDTO requestPostDTO, List<MultipartFile> images, Authentication authentication) throws PostNotFoundException {
        var postOwner = userService.findUserByAuth(authentication);
        var postEntity = postRepository.findByIdentifier(requestPostDTO.identifier());
        if (!(postEntity.getAuthor() == postOwner)) {
            throw new PostNotFoundException();
        }
        var DTOImages = imageService.updateImagesForPost(images, postEntity.getIdentifier(), authentication);
        Set<TagEntity> tags = tagService.getExistingTagsFromText(requestPostDTO.description());
        Set<TagEntity> nonExistingTags = tagService.saveNonExistingTagsFromText(requestPostDTO.description());
        tags.addAll(nonExistingTags);
        postEntity.setTags(tags);
        postEntity.setTitle(requestPostDTO.title());
        postEntity.setDescription(requestPostDTO.description());
        postRepository.save(postEntity);
        return new ResponsePostDTO(postEntity, DTOImages, imageService.getImageByUser(postEntity.getAuthor().getUsername()));
    }

    @Override
    public Map<String, Object> getPosts(int pageNumber) {
        var pageable = PageRequest.of(pageNumber, valueConfig.getPageSize() -40);
        var posts = postRepository.findAll(pageable);
        var postDtoPage = posts.map(post -> new ResponsePostDTO(post, imageService.getImagesByPost(post),
               imageService.getImageByUser(post.getAuthor().getUsername())));
        return pageConverter.convertPageToResponse(postDtoPage);
    }

    @Override
    public Map<String, Object> getPostsByTag(String tagName, int page) {
        var tag = tagRepository.findByName(tagName);
        var pageable = PageRequest.of(page, valueConfig.getPageSize());
        var posts = postRepository.getPostEntitiesByTagsContaining(tag, pageable);
        var postDTOPage = posts.map(post -> new ResponsePostDTO(post, imageService.getImagesByPost(post),
                imageService.getImageByUser(post.getAuthor().getUsername())));
        return pageConverter.convertPageToResponse(postDTOPage);
    }

    @Override
    public Map<String, Object> getPostsByUsername(String username, int page) {
        var userEntity = userRepository.findByUsername(username);
        var pageable = PageRequest.of(page, valueConfig.getPageSize());
        var posts = postRepository.findPostEntitiesByAuthor(userEntity, pageable);
        var postDTOPage = posts
                .map(post -> new ResponsePostDTO(post, imageService.getImagesByPost(post),
                        imageService.getImageByUser(post.getAuthor().getUsername())));
        return pageConverter.convertPageToResponse(postDTOPage);
    }

    @Override
    public Map<String, Object> getPostsByUserFollowing(Authentication authentication, int pageNumber) {
        var user = userService.findUserByAuth(authentication);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        var pageable = PageRequest.of(pageNumber, valueConfig.getPageSize());
        var followedUsers = user.getFollowing();
        var postsFromFollowedUsers = postRepository.findPostsByAuthorInAndCreationTimeBetween(
                followedUsers, getStartTime(), getCurrentTime(), pageable);
        var postDTOForPage = postsFromFollowedUsers.stream().parallel()
                .map(post -> new ResponsePostDTO(post, imageService.getImagesByPost(post),
                        imageService.getImageByUser(post.getAuthor().getUsername()))).collect(Collectors.toList());
        return pageConverter.convertPageToResponse(postsFromFollowedUsers, postDTOForPage);
    }

    @Override
    public Map<String, Object> findPostsByTextContaining(String title, int pageNumber) {
        var pageable = PageRequest.of(pageNumber, valueConfig.getPageSize(), Sort.by(Sort.Direction.ASC, "title"));
        var matchedPosts = postRepository.findPostEntitiesByTitleContaining(title, pageable);
        var postDtoPage = matchedPosts.map(post -> new ResponsePostDTO(post, imageService.getImagesByPost(post),
                imageService.getImageByUser(post.getAuthor().getUsername())));
        return pageConverter.convertPageToResponse(postDtoPage);
    }

    @Override
    public PostDTO getPostById(String identifier) {
        var post = postRepository.getPostEntityByIdentifier(identifier);
        var postImages = imageService.getImagesByPost(post);
        var userImage = imageService.getImageByUser(post.getAuthor().getUsername());
        var commentPage = commentService.getCommentsByPostIdentifier(identifier, 0);
        return new PostDTO(post, postImages, userImage, commentPage);
    }

    public Timestamp getStartTime() {
        var instant = Instant.now();
        return Timestamp.from(instant.minusSeconds(TWELVE_HOURS));
    }

    public Timestamp getCurrentTime() {
        var instant = Instant.now();
        return Timestamp.from(instant);
    }
}