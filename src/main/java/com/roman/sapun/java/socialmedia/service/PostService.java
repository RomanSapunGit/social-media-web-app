package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.post.PostDTO;
import com.roman.sapun.java.socialmedia.dto.post.RequestPostDTO;
import com.roman.sapun.java.socialmedia.dto.post.ResponsePostDTO;
import com.roman.sapun.java.socialmedia.exception.PostNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface PostService {
    /**
     * Creates a new post with the given request data, images, and authentication.
     *
     * @param requestPostDTO The DTO containing the post data.
     * @param images         The list of images associated with the post.
     * @param authentication The authentication object for the current user.
     * @return The DTO representing the created post.
     */
    ResponsePostDTO createPost(RequestPostDTO requestPostDTO, List<MultipartFile> images, Authentication authentication);

    /**
     * Updates an existing post with the given request data, images, and authentication.
     *
     * @param requestPostDTO The DTO containing the updated post data.
     * @param images         The list of images associated with the post.
     * @param authentication The authentication object for the current user.
     * @return The DTO representing the updated post.
     * @throws PostNotFoundException If the post with the given identifier is not found or Authors and User's data does not match.
     */
    ResponsePostDTO updatePost(RequestPostDTO requestPostDTO, List<MultipartFile> images, Authentication authentication) throws PostNotFoundException;

    /**
     * Retrieves a paginated list of posts.
     *
     * @param pageNumber The page number to retrieve.
     * @return map containing 50 posts, overall number of comments, current comment page and overall number of pages.
     */
    Map<String, Object> getPosts(int pageNumber);

    /**
     * Retrieves a paginated list of posts filtered by a specific tag.
     *
     * @param tagName The name of the tag to filter by.
     * @param page    The page number to retrieve.
     * @return map containing 50 posts, overall number of comments, current comment page and overall number of pages.
     */
    Map<String, Object> getPostsByTag(String tagName, int page);
    /**
     * Retrieves a paginated list of posts created by a specific user.
     *
     * @param username The username of the user.
     * @param page The page number to retrieve.
     * @return map containing 50 posts, overall number of comments, current comment page and overall number of pages.
     */
    Map<String, Object> getPostsByUsername(String username, int page);
    /**
     * Retrieves a paginated list of posts created by users that the authenticated user is following.
     *
     * @param authentication The authentication object for the current user.
     * @param pageNumber The page number to retrieve.
     * @return map containing 50 posts, overall number of comments, current comment page and overall number of pages.
     * @throws IllegalArgumentException If the user is not found.
     */
    Map<String, Object> getPostsByUserFollowing(Authentication authentication, int pageNumber);
    /**
     * Retrieves a paginated list of posts that contain the specified text in their title.
     *
     * @param title The text to search for in the post titles.
     * @param pageNumber The page number to retrieve.
     * @return map containing 50 posts, overall number of comments, current comment page and overall number of pages.
     */
    Map<String, Object> findPostsByTextContaining(String title, int pageNumber);
    /**
     * Retrieves a post by its identifier.
     *
     * @param identifier The identifier of the post.
     * @return The DTO representing the post.
     */
    @SuppressWarnings("unchecked")
    ResponsePostDTO getPostById(String identifier);
}
