package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.RequestPostDTO;
import com.roman.sapun.java.socialmedia.dto.PostDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PostService {
    PostDTO createPost(RequestPostDTO requestPostDTO, Authentication authentication);

    List<PostDTO> findPostsByTitleContaining(String title);

    List<PostDTO> findPostsByTags(String text);
}
