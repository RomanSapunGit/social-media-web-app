package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.RequestPostDTO;
import com.roman.sapun.java.socialmedia.dto.PostDTO;
import com.roman.sapun.java.socialmedia.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PostDTO createPost(@RequestBody RequestPostDTO requestPostDTO, Authentication authentication) {
        return postService.createPost(requestPostDTO, authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search-by-title")
    public List<PostDTO> findPostsByTitleContaining(@RequestParam String title) {
        return postService.findPostsByTitleContaining(title);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search-by-tags") //TODO write encoding stuff in a frontend(# -> %23)
    public List<PostDTO> findPostsByTags(@RequestParam String tags) {
        return postService.findPostsByTags(tags);
    }
}