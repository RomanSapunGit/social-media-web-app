package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.post.RequestPostDTO;
import com.roman.sapun.java.socialmedia.dto.post.PostDTO;
import com.roman.sapun.java.socialmedia.exception.PostNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PostService {
    PostDTO createPost(RequestPostDTO requestPostDTO, Authentication authentication);

    PostDTO updatePost(RequestPostDTO requestPostDTO, Authentication authentication) throws PostNotFoundException;

    Map<String, Object> getPosts(int pageNumber);

    Map<String, Object> getPostsByTag(String tagName, int page);

    Map<String, Object> getPostsByUsername(String username, int page);

    Map<String, Object> findPostsByTitleContaining(String title, int pageNumber);

}
