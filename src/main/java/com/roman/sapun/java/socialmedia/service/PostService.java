package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.RequestPostDTO;
import com.roman.sapun.java.socialmedia.dto.PostDTO;
import org.springframework.security.core.Authentication;

public interface PostService {
    PostDTO createPost(RequestPostDTO requestPostDTO, Authentication authentication);
}
