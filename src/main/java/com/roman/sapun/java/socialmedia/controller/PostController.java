package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.post.PostDTO;
import com.roman.sapun.java.socialmedia.dto.post.RequestPostDTO;
import com.roman.sapun.java.socialmedia.dto.post.ResponsePostDTO;
import com.roman.sapun.java.socialmedia.exception.PostNotFoundException;
import com.roman.sapun.java.socialmedia.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/post")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * Creates a new post.
     *
     * @param requestPostDTO The DTO containing the details of the post.
     * @param images         The list of image files associated with the post.
     * @param authentication The authentication object representing the current user.
     * @return The DTO representing the created post.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public ResponsePostDTO createPost(@ModelAttribute RequestPostDTO requestPostDTO,
                                      @RequestPart("images") List<MultipartFile> images, Authentication authentication) {
        return postService.createPost(requestPostDTO, images, authentication);
    }

    /**
     * Updates an existing post.
     *
     * @param requestPostDTO The DTO containing the updated details of the post.
     * @param images         The list of image files associated with the post.
     * @param authentication The authentication object representing the current user.
     * @return The DTO representing the updated post.
     * @throws PostNotFoundException If the post with the given identifier is not found or Authors and User's data does not match.
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping()
    public ResponsePostDTO updatePost(@ModelAttribute RequestPostDTO requestPostDTO, @RequestPart("images") List<MultipartFile> images,
                                      Authentication authentication) throws PostNotFoundException {
        return postService.updatePost(requestPostDTO, images, authentication);
    }

    /**
     * Retrieves posts that contain the specified text in their title.
     *
     * @param title The text to search for in the post titles.
     * @param page  The page number of the results.
     * @return map that contains 50 comments, overall number of comments, current comment page and overall number of pages.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search/{title}")
    public Map<String, Object> getPostsByTextContaining(@PathVariable String title, @RequestParam int page) {
        return postService.findPostsByTextContaining(title, page);
    }

    /**
     * Retrieves posts that are associated with the specified tag.
     *
     * @param tag  The tag to search for.
     * @param page The page number of the results.
     * @return map that contains 50 comments, overall number of comments, current comment page and overall number of pages.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/tag/{tag}")
    public Map<String, Object> getPostsByTag(@PathVariable String tag, @RequestParam int page) {
        return postService.getPostsByTag(tag, page);
    }

    /**
     * Retrieves posts that are created by the specified user.
     *
     * @param username The username of the user.
     * @param page     The page number of the results.
     * @return A map containing the posts that are created by the specified user and page.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/author/{username}")
    public Map<String, Object> getPostsByUsername(@PathVariable String username, @RequestParam int page) {
        return postService.getPostsByUsername(username, page);
    }

    /**
     * Retrieves posts that are subscribed by the authenticated user.
     *
     * @param authentication The authentication object of the authenticated user.
     * @param page           The page number of the results.
     * @return A map containing the posts that are subscribed by the authenticated user and page.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/follower")
    public Map<String, Object> getPostsBySubscription(Authentication authentication, @RequestParam int page) {
        return postService.getPostsByUserFollowing(authentication, page);
    }

    /**
     * Retrieves all posts using pagination.
     *
     * @param page The page number of the results.
     * @return A map containing all the posts and page.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search")
    public Map<String, Object> getPosts(@RequestParam int page) {
        return postService.getPosts(page);
    }

    /**
     * Retrieves a specific post by its identifier.
     *
     * @param identifier The identifier of the post.
     * @return The PostDTO object representing the specified post.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{identifier}")
    public PostDTO getPostById(@PathVariable String identifier) {
        return postService.getPostById(identifier);
    }
}