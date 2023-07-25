package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.post.PostDTO;
import com.roman.sapun.java.socialmedia.dto.post.RequestPostDTO;
import com.roman.sapun.java.socialmedia.exception.PostNotFoundException;
import com.roman.sapun.java.socialmedia.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/post")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public PostDTO createPost(@RequestBody RequestPostDTO requestPostDTO, Authentication authentication) {
        return postService.createPost(requestPostDTO, authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public PostDTO updatePost(@RequestBody RequestPostDTO requestPostDTO, Authentication authentication) throws PostNotFoundException {
        return postService.updatePost(requestPostDTO, authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/title")
    public Map<String, Object> findPostsByTitleContaining(@RequestParam String title, @RequestParam int page) {
        return postService.findPostsByTitleContaining(title, page);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{tag}")
    public Map<String, Object> findPostsByTag(@PathVariable String tag, @RequestParam int page) {
        return postService.getPostsByTag(tag, page);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/author/{username}")
    public Map<String, Object> findPostsByUsername(@PathVariable String username, @RequestParam int page) {
        return postService.getPostsByUsername(username, page);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search")
    public Map<String, Object> findPosts(@RequestParam int page) {
        return postService.getPosts(page);
    }
}