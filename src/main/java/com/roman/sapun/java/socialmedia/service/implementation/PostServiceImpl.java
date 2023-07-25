package com.roman.sapun.java.socialmedia.service.implementation;

import com.roman.sapun.java.socialmedia.config.ValueConfig;
import com.roman.sapun.java.socialmedia.dto.post.RequestPostDTO;
import com.roman.sapun.java.socialmedia.dto.post.PostDTO;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.TagEntity;
import com.roman.sapun.java.socialmedia.exception.PostNotFoundException;
import com.roman.sapun.java.socialmedia.repository.PostRepository;
import com.roman.sapun.java.socialmedia.repository.TagRepository;
import com.roman.sapun.java.socialmedia.repository.UserRepository;
import com.roman.sapun.java.socialmedia.service.PostService;
import com.roman.sapun.java.socialmedia.service.TagService;
import com.roman.sapun.java.socialmedia.service.UserService;
import com.roman.sapun.java.socialmedia.util.converter.PageConverter;
import com.roman.sapun.java.socialmedia.util.converter.PostConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;


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


    public PostServiceImpl(TagService tagService, PostConverter postConverter, UserService userService,
                           PostRepository postRepository, PageConverter pageConverter, ValueConfig valueConfig,
                           TagRepository tagRepository, UserRepository userRepository) {
        this.tagService = tagService;
        this.postConverter = postConverter;
        this.userService = userService;
        this.postRepository = postRepository;
        this.pageConverter = pageConverter;
        this.valueConfig = valueConfig;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }


    @Override
    public PostDTO createPost(RequestPostDTO requestPostDTO, Authentication authentication) {
        Set<TagEntity> existingTags = tagService.getExistingTagsFromText(requestPostDTO.description());
        Set<TagEntity> nonExistingTags = tagService.saveNonExistingTagsFromText(requestPostDTO.description());
        existingTags.addAll(nonExistingTags);
        var postOwner = userService.findUserByAuth(authentication);
        var postEntity = postConverter.convertToPostEntity(requestPostDTO, existingTags, postOwner, new PostEntity());
        postRepository.save(postEntity);
        return new PostDTO(postEntity);
    }

    @Override
    public PostDTO updatePost(RequestPostDTO requestPostDTO, Authentication authentication) throws PostNotFoundException {
        Set<TagEntity> tags = tagService.getExistingTagsFromText(requestPostDTO.description());
        Set<TagEntity> nonExistingTags = tagService.saveNonExistingTagsFromText(requestPostDTO.description());
        tags.addAll(nonExistingTags);
        var postOwner = userService.findUserByAuth(authentication);
        var postEntity = postRepository.findByIdentifier(requestPostDTO.identifier());
        if (!(postEntity.getAuthor() == postOwner)) {
            throw new PostNotFoundException();
        }
        postEntity.setTags(tags);
        postEntity.setTitle(requestPostDTO.title());
        postEntity.setDescription(requestPostDTO.description());
        postRepository.save(postEntity);
        return new PostDTO(postEntity);
    }

    @Override
    public Map<String, Object> getPosts(int pageNumber) {
        var pageable = PageRequest.of(pageNumber, valueConfig.getPageSize());
        var posts = postRepository.findAll(pageable);
        var postDtoPage = posts.map(PostDTO::new);
        return pageConverter.convertPageToResponse(postDtoPage);
    }

    @Override
    public Map<String, Object> getPostsByTag(String tagName, int page) {
        var tag = tagRepository.findByName(tagName);
        var pageable = PageRequest.of(page, valueConfig.getPageSize());
        var posts = postRepository.getPostEntitiesByTagsContaining(tag, pageable);
        var postDTOPage = posts.map(PostDTO::new);
        return pageConverter.convertPageToResponse(postDTOPage);
    }

    @Override
    public Map<String, Object> getPostsByUsername(String username, int page) {
        var userEntity = userRepository.findByUsername(username);
        var pageable = PageRequest.of(page, valueConfig.getPageSize());
        var posts = postRepository.findPostEntitiesByAuthor(userEntity, pageable);
        var postDTOPage = posts.map(PostDTO::new);
        return pageConverter.convertPageToResponse(postDTOPage);
    }

    @Override
    public Map<String, Object> findPostsByTitleContaining(String title, int pageNumber) {
        var pageable = PageRequest.of(pageNumber, valueConfig.getPageSize(), Sort.by(Sort.Direction.ASC, "title"));
        var matchedPosts = postRepository.findPostEntitiesByTitleContaining(title, pageable);
        var postDtoPage = matchedPosts.map(PostDTO::new);
        return pageConverter.convertPageToResponse(postDtoPage);
    }
}