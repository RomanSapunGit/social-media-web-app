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
import com.roman.sapun.java.socialmedia.exception.InvalidPageSizeException;
import com.roman.sapun.java.socialmedia.exception.PostNotFoundException;
import com.roman.sapun.java.socialmedia.exception.TagNotFoundException;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
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
    private final SSEController sseController;
    private final static int MAX_PAGE_SIZE = 35;


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
    }

    @Override
    public ResponsePostDTO createPost(RequestPostDTO requestPostDTO, List<MultipartFile> images, Authentication authentication) throws UserNotFoundException {
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
        return postConverter.convertToResponsePostDTO(postEntity);
    }

    @Override
    public ResponsePostDTO updatePost(RequestPostDTO requestPostDTO, List<MultipartFile> images, Authentication authentication) throws PostNotFoundException, UserNotFoundException {
        var postOwner = userService.findUserByAuth(authentication);
        var postEntity = postRepository.findByIdentifier(requestPostDTO.identifier()).orElseThrow(PostNotFoundException::new);
        if (!(postEntity.getAuthor().equals(postOwner))) {
            throw new PostNotFoundException();
        }
        Set<TagEntity> tags = tagService.getExistingTagsFromText(requestPostDTO.description());
        Set<TagEntity> nonExistingTags = tagService.saveNonExistingTagsFromText(requestPostDTO.description());
        tags.addAll(nonExistingTags);
        postEntity.setTags(tags);
        postEntity.setTitle(requestPostDTO.title());
        postEntity.setDescription(requestPostDTO.description());
        postRepository.save(postEntity);
        var postDTO = postConverter.convertToResponsePostDTO(postEntity);
        sseController.sendPostUpdate(postDTO.identifier(), postDTO);
        return postDTO;
    }

    @Override
    public Map<String, Object> getPosts(int pageNumber, int pageSize, String sortByValue) throws InvalidPageSizeException {
        validatePageSize(pageSize);
        var pageRequest = setPageable(pageNumber, pageSize, sortByValue);
        var posts = postRepository.findAll(pageRequest);
        return convertPageToResponsePostDTO(posts);
    }

    @Override
    public Map<String, Object> getPostsByTag(String tagName, int page, int pageSize, String sortByValue) throws InvalidPageSizeException, TagNotFoundException {
        validatePageSize(pageSize);
        var pageRequest = setPageable(page, pageSize, sortByValue);
        var tag = tagRepository.findByName(tagName).orElseThrow(TagNotFoundException::new);
        var posts = postRepository.getPostEntitiesByTagsContaining(tag, pageRequest);
        return convertPageToResponsePostDTO(posts);
    }

    @Override
    public Map<String, Object> getPostsByUsername(String username, int page, int pageSize, String sortByValue) throws InvalidPageSizeException, UserNotFoundException {
        validatePageSize(pageSize);
        var pageRequest = setPageable(page, pageSize, sortByValue);
        var userEntity = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        var posts = postRepository.findPostEntitiesByAuthor(userEntity, pageRequest);
        return convertPageToResponsePostDTO(posts);
    }

    @Override
    public Map<String, Object> getPostsByUserFollowing(Authentication authentication, int pageNumber, int pageSize, String sortByValue) throws UserNotFoundException, InvalidPageSizeException {
        validatePageSize(pageSize);
        var user = userService.findUserByAuth(authentication);
        var pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("creationTime").descending());
        var followedUsers = user.getFollowing();
        var postsFromFollowedUsers = postRepository.findPostsByAuthorInAndCreationTimeBetween(
                followedUsers, getStartTime(), getCurrentTime(), pageRequest);
        var postDTOForPage = postsFromFollowedUsers.stream()
                .map(postConverter::convertToResponsePostDTO)
                .collect(Collectors.toList());
        return pageConverter.convertPageToResponse(postsFromFollowedUsers, postDTOForPage);
    }

    @Override
    public Map<String, Object> findPostsByTextContaining(String title, int pageNumber, int pageSize, String sortByValue) throws InvalidPageSizeException {
        validatePageSize(pageSize);
        var pageRequest = setPageable(pageNumber, pageSize, sortByValue);
        var matchedPosts = postRepository.findPostEntitiesByTitleContaining(title, pageRequest);
        return convertPageToResponsePostDTO(matchedPosts);
    }

    @Override
    public ResponsePostDTO getPostById(String identifier) throws PostNotFoundException, UserNotFoundException {
        var post = postRepository.getPostEntityByIdentifier(identifier).orElseThrow(PostNotFoundException::new);
        return postConverter.convertToResponsePostDTO(post);
    }

    @Override
    public ResponsePostDTO deletePostByIdentifier(String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException {
        var post = postRepository.findByIdentifier(identifier).orElseThrow(PostNotFoundException::new);
        var user = userService.findUserByAuth(authentication);

        if (!(post.getAuthor().getUsername().equals(user.getUsername()))) {
            throw new PostNotFoundException();
        }

        postRepository.delete(post);
        return postConverter.convertToResponsePostDTO(post);
    }

    public Timestamp getStartTime() {
        var instant = Instant.now();
        return Timestamp.from(instant.minusSeconds(valueConfig.getTwelveHours()));
    }

    public Timestamp getCurrentTime() {
        var instant = Instant.now();
        return Timestamp.from(instant);
    }

    private Pageable setPageable(int pageNumber, int pageSize, String sortByValue) throws InvalidPageSizeException {
        validatePageSize(pageSize);
        var sortByParts = sortByValue.split(" ");
        var sortByField = sortByParts[0].replaceAll("\\s+", "");

        return sortByValue.contains("asc") ? PageRequest.of(pageNumber, pageSize, Sort.by(sortByField).ascending()) :
                PageRequest.of(pageNumber, pageSize, Sort.by(sortByField).descending());
    }

    private void validatePageSize(int pageSize) throws InvalidPageSizeException {
        if (pageSize >= MAX_PAGE_SIZE) {
            throw new InvalidPageSizeException("Page size must be less than " + MAX_PAGE_SIZE);
        }
    }

    private Map<String, Object> convertPageToResponsePostDTO(Page<PostEntity> posts) {
        var content = posts.getContent().stream().map(postConverter::convertToResponsePostDTO)
                .filter(post -> post.userImage() != null).toList();
        return pageConverter.convertPageToResponse(posts, content, content.size());
    }
}