package com.roman.sapun.java.socialmedia.service.implementation;

import com.roman.sapun.java.socialmedia.dto.RequestPostDTO;
import com.roman.sapun.java.socialmedia.dto.PostDTO;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.TagEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.repository.PostRepository;
import com.roman.sapun.java.socialmedia.service.PostService;
import com.roman.sapun.java.socialmedia.service.TagService;
import com.roman.sapun.java.socialmedia.service.UserService;
import com.roman.sapun.java.socialmedia.util.PostConverter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final TagService tagService;
    private final UserService userService;
    private final PostConverter postConverter;
    private final PostRepository postRepository;

    public PostServiceImpl(TagService tagService, PostConverter postConverter, UserService userService,
                           PostRepository postRepository) {
        this.tagService = tagService;
        this.postConverter = postConverter;
        this.userService = userService;
        this.postRepository = postRepository;
    }

    @Override
    public PostDTO createPost(RequestPostDTO requestPostDTO, Authentication authentication) {
        Set<TagEntity> existingTags = tagService.getExistingTagsFromText(requestPostDTO.description());
        Set<TagEntity> nonExistingTags = tagService.saveNonExistingTagsFromText(requestPostDTO.description());
        existingTags.addAll(nonExistingTags);
        UserEntity postOwner = userService.findUserByAuth(authentication);
        PostEntity postEntity = postConverter.convertToPostEntity(requestPostDTO, existingTags, postOwner);
        postRepository.save(postEntity);
        return new PostDTO(requestPostDTO, postEntity.getCreationTime());
    }

    @Override
    public List<PostDTO> findPostsByTitleContaining(String title) {
        return postRepository.findPostEntitiesByTitleContaining(title).stream()
                .map(PostDTO::new)
                .toList();
    }

    @Override
    public List<PostDTO> findPostsByTags(String tag) {
        Set<TagEntity> existingTags = tagService.getExistingTagsFromText(tag);
        List<PostEntity> matchedPosts = postRepository.findPostEntitiesByTagsIn(existingTags);
        return matchedPosts.stream()
                .filter(post -> post.getTags().containsAll(existingTags))
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }

}