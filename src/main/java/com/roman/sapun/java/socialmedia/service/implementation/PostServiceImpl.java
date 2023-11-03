package com.roman.sapun.java.socialmedia.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.roman.sapun.java.socialmedia.config.ValueConfig;
import com.roman.sapun.java.socialmedia.controller.SSEController;
import com.roman.sapun.java.socialmedia.dto.credentials.ValidatorDTO;
import com.roman.sapun.java.socialmedia.dto.post.RequestPostDTO;
import com.roman.sapun.java.socialmedia.dto.post.ResponsePostDTO;
import com.roman.sapun.java.socialmedia.dto.user.ResponseUserDTO;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.TagEntity;
import com.roman.sapun.java.socialmedia.exception.PostNotFoundException;
import com.roman.sapun.java.socialmedia.repository.PostRepository;
import com.roman.sapun.java.socialmedia.repository.TagRepository;
import com.roman.sapun.java.socialmedia.repository.UserRepository;
import com.roman.sapun.java.socialmedia.service.*;
import com.roman.sapun.java.socialmedia.util.TextExtractor;
import com.roman.sapun.java.socialmedia.util.converter.PageConverter;
import com.roman.sapun.java.socialmedia.util.converter.PostConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PostServiceImpl implements PostService, VoteService {

    private final TagService tagService;
    private final UserService userService;
    private final PostConverter postConverter;
    private final PostRepository postRepository;
    private final PageConverter pageConverter;
    private final ValueConfig valueConfig;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final SSEController sseController;
    private final TextExtractor textExtractor;


    public PostServiceImpl(TagService tagService, PostConverter postConverter, UserService userService,
                           PostRepository postRepository, PageConverter pageConverter, ValueConfig valueConfig,
                           TagRepository tagRepository, UserRepository userRepository, ImageService imageService,
                           SSEController sseController, TextExtractor textExtractor) {
        this.tagService = tagService;
        this.postConverter = postConverter;
        this.userService = userService;
        this.postRepository = postRepository;
        this.pageConverter = pageConverter;
        this.valueConfig = valueConfig;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.imageService = imageService;
        this.sseController = sseController;
        this.textExtractor = textExtractor;
    }

    @Override
    public ResponsePostDTO createPost(RequestPostDTO requestPostDTO, List<MultipartFile> images, Authentication authentication) {
        Set<TagEntity> existingTags = tagService.getExistingTagsFromText(requestPostDTO.description());
        Set<TagEntity> nonExistingTags = tagService.saveNonExistingTagsFromText(requestPostDTO.description());
        existingTags.addAll(nonExistingTags);
        var postOwner = userService.findUserByAuth(authentication);
        var postEntity = postConverter.convertToPostEntity(requestPostDTO, existingTags, postOwner, new PostEntity(), new HashSet<>());
        postRepository.save(postEntity);
        if (images != null) {
            var dtoImages = imageService.uploadImagesForPost(images, postEntity.getIdentifier(), authentication);
            return new ResponsePostDTO(postEntity,
                    dtoImages,
                    imageService.getImageByUser(postEntity.getAuthor().getUsername()),
                    new HashSet<>(),
                    new HashSet<>());
        }
        return new ResponsePostDTO(postEntity, new ArrayList<>(), imageService.getImageByUser(postEntity.getAuthor().getUsername()),
                postEntity.getUpvotes().stream().map(ResponseUserDTO::new).collect(Collectors.toSet()),
                postEntity.getDownvotes().stream().map(ResponseUserDTO::new).collect(Collectors.toSet()));
    }

    @Override
    public ResponsePostDTO updatePost(RequestPostDTO requestPostDTO, List<MultipartFile> images, Authentication authentication) throws PostNotFoundException {
        var postOwner = userService.findUserByAuth(authentication);
        var postEntity = postRepository.findByIdentifier(requestPostDTO.identifier());
        if (!(postEntity.getAuthor().equals(postOwner))) {
            throw new PostNotFoundException();
        }
        var dtoImages = imageService.updateImagesForPost(images, postEntity.getIdentifier(), authentication);
        Set<TagEntity> tags = tagService.getExistingTagsFromText(requestPostDTO.description());
        Set<TagEntity> nonExistingTags = tagService.saveNonExistingTagsFromText(requestPostDTO.description());
        tags.addAll(nonExistingTags);
        postEntity.setTags(tags);
        postEntity.setTitle(requestPostDTO.title());
        postEntity.setDescription(requestPostDTO.description());
        postRepository.save(postEntity);
        var postDTO = new ResponsePostDTO(postEntity, dtoImages, imageService.getImageByUser(postEntity.getAuthor().getUsername()),
                postEntity.getUpvotes().stream().map(ResponseUserDTO::new).collect(Collectors.toSet()),
                postEntity.getDownvotes().stream().map(ResponseUserDTO::new).collect(Collectors.toSet()));
        sseController.sendPostUpdate(postDTO.identifier(), postDTO);
        return postDTO;
    }

    @Override
    public Map<String, Object> getPosts(int pageNumber, int pageSize, String sortByValue) {
        var pageable = setPageable(pageNumber, pageSize, sortByValue);
        var posts = postRepository.findAll(pageable);
        return convertPageToResponsePostDTO(posts);
    }

    @Override
    public Map<String, Object> getPostsByTag(String tagName, int page, int pageSize, String sortByValue) {
        var pageable = setPageable(page, pageSize, sortByValue);
        var tag = tagRepository.findByName(tagName);
        var posts = postRepository.getPostEntitiesByTagsContaining(tag, pageable);
        return convertPageToResponsePostDTO(posts);
    }

    @Override
    public Map<String, Object> getPostsByUsername(String username, int page, int pageSize, String sortByValue) {
        var pageable = setPageable(page, pageSize, sortByValue);
        var userEntity = userRepository.findByUsername(username);

        var posts = postRepository.findPostEntitiesByAuthor(userEntity, pageable);
        return convertPageToResponsePostDTO(posts);
    }

    private Map<String, Object> convertPageToResponsePostDTO(Page<PostEntity> posts) {
        var postDTOPage = posts
                .map(post -> new ResponsePostDTO(post, imageService.getImagesByPost(post),
                        imageService.getImageByUser(post.getAuthor().getUsername()),
                        post.getUpvotes().stream().map(ResponseUserDTO::new).collect(Collectors.toSet()),
                        post.getDownvotes().stream().map(ResponseUserDTO::new).collect(Collectors.toSet())));
        return pageConverter.convertPageToResponse(postDTOPage);
    }

    @Override
    public Map<String, Object> getPostsByUserFollowing(Authentication authentication, int pageNumber, int pageSize, String sortByValue) {
        if (pageSize >= 35) {
            throw new IllegalArgumentException("Page size must be less than 35");
        }
        var user = userService.findUserByAuth(authentication);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        var pageable = PageRequest.of(pageNumber, pageSize, Sort.by("creationTime").descending());
        var followedUsers = user.getFollowing();
        var postsFromFollowedUsers = postRepository.findPostsByAuthorInAndCreationTimeBetween(
                followedUsers, getStartTime(), getCurrentTime(), pageable);
        var postDTOForPage = postsFromFollowedUsers.stream().parallel()
                .map(post -> new ResponsePostDTO(post, imageService.getImagesByPost(post),
                        imageService.getImageByUser(post.getAuthor().getUsername()),
                        post.getUpvotes().stream().map(ResponseUserDTO::new).collect(Collectors.toSet()),
                        post.getDownvotes().stream().map(ResponseUserDTO::new).collect(Collectors.toSet())))
                .collect(Collectors.toList());
        return pageConverter.convertPageToResponse(postsFromFollowedUsers, postDTOForPage);
    }

    @Override
    public Map<String, Object> findPostsByTextContaining(String title, int pageNumber, int pageSize, String sortByValue) {
        var pageable = setPageable(pageNumber, pageSize, sortByValue);
        var matchedPosts = postRepository.findPostEntitiesByTitleContaining(title, pageable);
        return convertPageToResponsePostDTO(matchedPosts);
    }

    @Override
    public ResponsePostDTO getPostById(String identifier) {
        var post = postRepository.getPostEntityByIdentifier(identifier);
        var postImages = imageService.getImagesByPost(post);
        var userImage = imageService.getImageByUser(post.getAuthor().getUsername());
        return new ResponsePostDTO(post, postImages, userImage,
                post.getUpvotes().stream().map(ResponseUserDTO::new).collect(Collectors.toSet()),
                post.getDownvotes().stream().map(ResponseUserDTO::new).collect(Collectors.toSet()));
    }

    @Override
    public Set<ResponseUserDTO> addUpvote(String identifier, Authentication authentication) throws JsonProcessingException {
        if (identifier == null) {
            throw new IllegalArgumentException("Identifier is null");
        }
        var identifierAsValue = textExtractor.extractIdentifierFromJson(identifier);
        var user = userService.findUserByAuth(authentication);
        var post = postRepository.findByIdentifier(identifierAsValue);
        post.getUpvotes().add(user);
        postRepository.save(post);
        return post.getUpvotes().stream().map(ResponseUserDTO::new).collect(Collectors.toSet());
    }

    @Override
    public Set<ResponseUserDTO> removeUpvote(String identifier, Authentication authentication) {
        var user = userService.findUserByAuth(authentication);
        var post = postRepository.findByIdentifier(identifier);
        post.getUpvotes().remove(user);
        postRepository.save(post);
        return post.getUpvotes().stream().map(ResponseUserDTO::new).collect(Collectors.toSet());
    }

    @Override
    public Set<ResponseUserDTO> addDownvote(String identifier, Authentication authentication) throws JsonProcessingException {
        var identifierAsValue = textExtractor.extractIdentifierFromJson(identifier);
        var user = userService.findUserByAuth(authentication);
        var post = postRepository.findByIdentifier(identifierAsValue);
        post.getDownvotes().add(user);
        postRepository.save(post);
        return post.getDownvotes().stream().map(ResponseUserDTO::new).collect(Collectors.toSet());
    }

    @Override
    public Set<ResponseUserDTO> removeDownvote(String identifier, Authentication authentication) {
        var user = userService.findUserByAuth(authentication);
        var post = postRepository.findByIdentifier(identifier);
        post.getDownvotes().remove(user);
        postRepository.save(post);
        return post.getDownvotes().stream().map(ResponseUserDTO::new).collect(Collectors.toSet());
    }

    @Override
    public ValidatorDTO isUpvoteMade(String identifier, Authentication authentication) {
        var user = userService.findUserByAuth(authentication);
        var post = postRepository.findByIdentifier(identifier);
        return new ValidatorDTO(post.getUpvotes().contains(user));
    }

    @Override
    public ValidatorDTO isDownvoteMade(String identifier, Authentication authentication) {
        var user = userService.findUserByAuth(authentication);
        var post = postRepository.findByIdentifier(identifier);
        return new ValidatorDTO(post.getDownvotes().contains(user));
    }

    public Timestamp getStartTime() {
        var instant = Instant.now();
        return Timestamp.from(instant.minusSeconds(valueConfig.getTwelveHours()));
    }

    public Timestamp getCurrentTime() {
        var instant = Instant.now();
        return Timestamp.from(instant);
    }

    private Pageable setPageable(int pageNumber, int pageSize, String sortByValue) {
        if (pageSize >= 35) {
            throw new IllegalArgumentException("Page size must be less than 35");
        }
        var sortByParts = sortByValue.split(" ");
        var sortByField = sortByParts[0].replaceAll("\\s+", "");

        return sortByValue.contains("asc") ? PageRequest.of(pageNumber, pageSize, Sort.by(sortByField).ascending()) :
                PageRequest.of(pageNumber, pageSize, Sort.by(sortByField).descending());
    }
}