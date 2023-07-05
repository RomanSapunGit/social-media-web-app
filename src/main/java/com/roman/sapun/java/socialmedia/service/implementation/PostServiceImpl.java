package com.roman.sapun.java.socialmedia.service.implementation;

import com.roman.sapun.java.socialmedia.config.ValueConfig;
import com.roman.sapun.java.socialmedia.dto.RequestPostDTO;
import com.roman.sapun.java.socialmedia.dto.PostDTO;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.TagEntity;
import com.roman.sapun.java.socialmedia.repository.PostRepository;
import com.roman.sapun.java.socialmedia.service.PostService;
import com.roman.sapun.java.socialmedia.service.TagService;
import com.roman.sapun.java.socialmedia.service.UserService;
import com.roman.sapun.java.socialmedia.util.converter.PageConverter;
import com.roman.sapun.java.socialmedia.util.converter.PostConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class PostServiceImpl implements PostService {

    private final TagService tagService;
    private final UserService userService;
    private final PostConverter postConverter;
    private final PostRepository postRepository;
    private final PageConverter pageConverter;
    private final ValueConfig valueConfig;

    public PostServiceImpl(TagService tagService, PostConverter postConverter, UserService userService,
                           PostRepository postRepository, PageConverter pageConverter, ValueConfig valueConfig) {
        this.tagService = tagService;
        this.postConverter = postConverter;
        this.userService = userService;
        this.postRepository = postRepository;
        this.pageConverter = pageConverter;
        this.valueConfig = valueConfig;
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
    public Map<String, Object> findPostsByTitleContaining(String title, int pageNumber) {
        var pageable = PageRequest.of(pageNumber, valueConfig.getPageSize(), Sort.by(Sort.Direction.ASC, "title"));
        var matchedPosts = postRepository.findPostEntitiesByTitleContaining(title, pageable);
        var postDtoPage = matchedPosts.map(PostDTO::new);
        return pageConverter.convertPageToResponse(postDtoPage);
    }

    @Override
    public Map<String, Object> findPostsByTags(String tag, int pageNumber) {
        var existingTags = tagService.getExistingTagsFromText(tag);
        var pageable = PageRequest.of(pageNumber, valueConfig.getPageSize(), Sort.by(Sort.Direction.ASC, "title"));
        var matchedPosts = postRepository.findPostEntitiesByTagsIn(existingTags, pageable);
        var postDtoPage = matchedPosts.map(PostDTO::new);
        return pageConverter.convertPageToResponse(postDtoPage);
    }
}