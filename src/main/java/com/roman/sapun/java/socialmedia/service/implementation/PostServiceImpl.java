package com.roman.sapun.java.socialmedia.service.implementation;

import com.roman.sapun.java.socialmedia.config.ValueConfig;
import com.roman.sapun.java.socialmedia.dto.image.RequestImageDTO;
import com.roman.sapun.java.socialmedia.dto.page.PostPageDTO;
import com.roman.sapun.java.socialmedia.dto.post.RequestPostDTO;
import com.roman.sapun.java.socialmedia.dto.post.ResponsePostDTO;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.TagEntity;
import com.roman.sapun.java.socialmedia.entity.UserStatisticsEntity;
import com.roman.sapun.java.socialmedia.exception.*;
import com.roman.sapun.java.socialmedia.repository.PostRepository;
import com.roman.sapun.java.socialmedia.repository.TagRepository;
import com.roman.sapun.java.socialmedia.repository.UserRepository;
import com.roman.sapun.java.socialmedia.service.*;
import com.roman.sapun.java.socialmedia.util.converter.ImageConverter;
import com.roman.sapun.java.socialmedia.util.converter.PageConverter;
import com.roman.sapun.java.socialmedia.util.converter.PostConverter;
import io.micrometer.observation.annotation.Observed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.dataloader.BatchLoader;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


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
    private final ImageConverter imageConverter;
    private final UserStatisticsService userStatisticsService;
    private final static int MAX_PAGE_SIZE = 35;


    public PostServiceImpl(TagService tagService, PostConverter postConverter, UserService userService,
                           PostRepository postRepository, PageConverter pageConverter, ValueConfig valueConfig,
                           TagRepository tagRepository, UserRepository userRepository, ImageService imageService,
                           ImageConverter imageConverter, UserStatisticsService userStatisticsService) {
        this.tagService = tagService;
        this.postConverter = postConverter;
        this.userService = userService;
        this.postRepository = postRepository;
        this.pageConverter = pageConverter;
        this.valueConfig = valueConfig;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.imageService = imageService;
        this.imageConverter = imageConverter;
        this.userStatisticsService = userStatisticsService;
    }

    @Override
    public ResponsePostDTO createPost(RequestPostDTO requestPostDTO, List<MultipartFile> images,
                                      Authentication authentication, HttpServletRequest request) throws Exception {

        Set<TagEntity> existingTags = tagService.getExistingTagsFromText(requestPostDTO.description());
        Set<TagEntity> nonExistingTags = tagService.saveNonExistingTagsFromText(requestPostDTO.description());
        existingTags.addAll(nonExistingTags);
        var postOwner = userService.findUserByAuth(authentication);
        var postEntity = postConverter.convertToPostEntity(requestPostDTO, existingTags, postOwner, new PostEntity(), new HashSet<>());
        var statistics = postOwner.getUserStatistics();
        postEntity.setUserStatistics(postOwner.getUserStatistics());
        postRepository.save(postEntity);
        var consent = postEntity.getAuthor().getUserStatistics().getConsent() == null ? "false" : postEntity.getAuthor().getUserStatistics().getConsent();
        if (consent.equals("true")) {
            userStatisticsService.addCreatedPostToStatistic(postOwner, postEntity, request);
        }
        if (images != null) {
            var dtoImages = imageService.uploadImagesForPost(images, postEntity.getIdentifier(), authentication);
            return new ResponsePostDTO(postEntity, dtoImages.join(), imageConverter.convertImageToDTO(postEntity.getAuthor().getImage()),
                    0, 0);
        }
        return postConverter.convertToResponsePostDTO(postEntity);
    }

    @Override
    public ResponsePostDTO updatePost(String identifier, String title, String description, List<RequestImageDTO> images,
                                      List<RequestImageDTO> newImages,
                                      Authentication authentication) throws PostNotFoundException, UserNotFoundException {
        var postOwner = userService.findUserByAuth(authentication);
        var postEntity = postRepository.findByIdentifier(identifier).orElseThrow(PostNotFoundException::new);
        if (!(postEntity.getAuthor().equals(postOwner))) {
            throw new PostNotFoundException();
        }
        Set<TagEntity> tags = tagService.getExistingTagsFromText(description);
        Set<TagEntity> nonExistingTags = tagService.saveNonExistingTagsFromText(description);
        tags.addAll(nonExistingTags);
        postEntity.setTags(tags);
        postEntity.setTitle(title);
        postEntity.setDescription(description);
        var imageEntities = imageConverter.convertImagesToEntity(newImages, postEntity);
        var existedImageEntities = postEntity.getImages().stream()
                .filter(imageEntity -> images.stream()
                        .anyMatch(requestImageDTO -> requestImageDTO.identifier().equals(imageEntity.getIdentifier())))
                .toList();
        imageEntities.addAll(existedImageEntities);
        postEntity.getImages().clear();
        postEntity.getImages().addAll(imageEntities);
        postRepository.save(postEntity);
        return postConverter.convertToResponsePostDTO(postEntity);
    }

    @Observed(name = "get.posts.time")
    @Override
    public PostPageDTO getPosts(int pageNumber, int pageSize, String sortByValue) throws InvalidPageSizeException {
        var pageRequest = setPageable(pageNumber, pageSize, sortByValue);
        var posts = postRepository.findAll(pageRequest);
        return pageConverter.convertPageToPostPageDTO(posts);
    }

    @Override
    public PostPageDTO getPostsByTag(String tagName, int page, int pageSize, String sortByValue) throws InvalidPageSizeException, TagNotFoundException {
        var pageRequest = setPageable(page, pageSize, sortByValue);
        var tag = tagRepository.findByName(tagName).orElseThrow(TagNotFoundException::new);
        var posts = postRepository.getPostEntitiesByTagsContaining(tag, pageRequest);
        return pageConverter.convertPageToPostPageDTO(posts);
    }

    @Override
    public PostPageDTO getPostsByUsername(String username, int page, int pageSize, String sortByValue) throws InvalidPageSizeException, UserNotFoundException {
        var pageRequest = setPageable(page, pageSize, sortByValue);
        var userEntity = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        var posts = postRepository.findPostEntitiesByAuthor(userEntity, pageRequest);
        return pageConverter.convertPageToPostPageDTO(posts);
    }

    @Override
    public PostPageDTO getSavedPosts(Authentication authentication, int pageNumber, int pageSize, String sortByValue) throws UserNotFoundException, InvalidPageSizeException {
        var user = userService.findUserByAuth(authentication);
        var savedPosts = user.getSavedPosts();

        List<PostEntity> savedPostsList = new ArrayList<>(savedPosts);

        Pageable pageable = setPageable(pageNumber, pageSize, sortByValue);

        Page<PostEntity> savedPostsPage = new PageImpl<>(savedPostsList, pageable, savedPostsList.size());

        return pageConverter.convertPageToPostPageDTO(savedPostsPage);
    }

    @Override
    public ResponsePostDTO removePostFromSavedList(String identifier, Authentication authentication) throws PostNotFoundException, UserNotFoundException {
        var user = userService.findUserByAuth(authentication);
        var postEntity = postRepository.findByIdentifier(identifier).orElseThrow(PostNotFoundException::new);
        user.getSavedPosts().remove(postEntity);
        userRepository.save(user);
        return postConverter.convertToResponsePostDTO(postEntity);
    }

    @Override
    public ResponsePostDTO addPostToSavedList(String identifier, Authentication authentication) throws PostNotFoundException, UserNotFoundException {
        var user = userService.findUserByAuth(authentication);
        var postEntity = postRepository.findByIdentifier(identifier).orElseThrow(PostNotFoundException::new);
        user.getSavedPosts().add(postEntity);
        userRepository.save(user);
        return postConverter.convertToResponsePostDTO(postEntity);
    }

    @Override
    public boolean isPostExistInSavedList(String identifier, Authentication authentication) throws PostNotFoundException, UserNotFoundException {
        var user = userService.findUserByAuth(authentication);
        var postEntity = postRepository.findByIdentifier(identifier).orElseThrow(PostNotFoundException::new);
        return user.getSavedPosts().stream().anyMatch(savedPost -> savedPost.getIdentifier().equals(postEntity.getIdentifier()));
    }

    @Override
    public PostPageDTO getPostsByUserFollowing(Authentication authentication, int pageNumber, int pageSize, String sortByValue) throws UserNotFoundException, InvalidPageSizeException {
        var user = userService.findUserByAuth(authentication);
        var pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("creationTime").descending());
        var followedUsers = user.getFollowing();
        var postsFromFollowedUsers = postRepository.findPostsByAuthorInAndCreationTimeBetween(
                followedUsers, getStartTime(), getCurrentTime(), pageRequest);
        return pageConverter.convertPageToPostPageDTO(postsFromFollowedUsers);
    }

    @Override
    public PostPageDTO findPostsByTextContaining(String title, int pageNumber, int pageSize, String sortByValue) throws InvalidPageSizeException {
        var pageRequest = setPageable(pageNumber, pageSize, sortByValue);
        var matchedPosts = postRepository.findPostEntitiesByTitleContaining(title, pageRequest);
        return pageConverter.convertPageToPostPageDTO(matchedPosts);
    }

    @Override
    public ResponsePostDTO getPostById(String identifier, HttpServletRequest request, Authentication authentication) throws PostNotFoundException, UserNotFoundException, UserStatisticsNotFoundException {
        var post = postRepository.getPostEntityByIdentifier(identifier).orElseThrow(PostNotFoundException::new);
        var viewer = userService.findUserByAuth(authentication);
        var consent = viewer.getUserStatistics().getConsent() == null ? "false" : viewer.getUserStatistics().getConsent();
        if (consent.equals("true")) {
            userStatisticsService.addViewedPostToStatistic(viewer, post, request);
        }
        return postConverter.convertToResponsePostDTO(post);
    }

    @Transactional
    @Override
    public ResponsePostDTO deletePostByIdentifier(String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException {
        var post = postRepository.findByIdentifier(identifier).orElseThrow(PostNotFoundException::new);
        var user = userService.findUserByAuth(authentication);
        if (!(post.getAuthor().getUsername().equals(user.getUsername()))) {
            throw new PostNotFoundException();
        }
        user.getSavedPosts().remove(post);
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
}