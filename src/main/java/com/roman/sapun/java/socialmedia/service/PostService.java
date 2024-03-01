package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.image.RequestImageDTO;
import com.roman.sapun.java.socialmedia.dto.post.RequestPostDTO;
import com.roman.sapun.java.socialmedia.dto.post.ResponsePostDTO;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public interface PostService {
    /**
     * Creates a new post with the given request data, images, and authentication.
     *
     * @param requestPostDTO The DTO containing the post data.
     * @param images         The list of images associated with the post.
     * @param authentication The authentication object for the current user.
     * @return The DTO representing the created post.
     */
    ResponsePostDTO createPost(RequestPostDTO requestPostDTO, List<MultipartFile> images, Authentication authentication, HttpServletRequest request) throws Exception;

    /**
     * Updates an existing post with the provided data, images, and authentication.
     * <p>
     * This method allows an authenticated user to modify an existing post by providing updated information,
     * including the post identifier, title, description, lists of existing and new images, and user authentication.
     *
     * @param identifier      The unique identifier of the post to be updated.
     * @param title           The updated title for the post.
     * @param description     The updated description for the post.
     * @param images          The list of existing images associated with the post.
     * @param newImages       The list of new images to be added to the post.
     * @param authentication  The authentication object for the current user.
     * @return The DTO representing the updated post.
     * @throws PostNotFoundException         If the post with the given identifier is not found or if the authors' and user's data does not match.
     * @throws UserNotFoundException         If the authenticated user is not found.
     * @throws InvalidImageNumberException    If the total number of images exceeds the allowed limit.
     */
    ResponsePostDTO updatePost(String identifier, String title, String description, List<RequestImageDTO> images,
                               List<RequestImageDTO> newImages,
                               Authentication authentication)
            throws PostNotFoundException, UserNotFoundException, InvalidImageNumberException;

    /**
     * Retrieves a paginated list of posts.
     *
     * @param pageNumber  The page number to retrieve.
     * @param pageSize    The number of posts to include on each page.
     * @param sortByValue The field by which to sort the posts.
     * @return A map containing 50 posts, overall number of comments, current comment page, and overall number of pages.
     */
    Page<PostEntity> getPosts(Authentication authentication, int pageNumber, int pageSize, String sortByValue) throws InvalidPageSizeException, UserNotFoundException, TagNotFoundException;

    /**
     * Retrieves a paginated list of posts filtered by a specific tag.
     *
     * @param tagName     The name of the tag to filter by.
     * @param page        The page number to retrieve.
     * @param pageSize    The number of posts to include on each page.
     * @param sortByValue The field by which to sort the posts.
     * @return A map containing 50 posts, overall number of comments, current comment page, and overall number of pages.
     */
    Page<PostEntity> getPostsByTag(String tagName, int page, int pageSize, String sortByValue) throws InvalidPageSizeException, TagNotFoundException, ExecutionException, InterruptedException;

    /**
     * Retrieves a paginated list of posts created by a specific user.
     *
     * @param username    The username of the user.
     * @param page        The page number to retrieve.
     * @param pageSize    The number of posts to include on each page.
     * @param sortByValue The field by which to sort the posts.
     * @return A map containing 50 posts, overall number of comments, current comment page, and overall number of pages.
     */
    Page<PostEntity> getPostsByUsername(String username, int page, int pageSize, String sortByValue) throws InvalidPageSizeException, UserNotFoundException;


    ResponsePostDTO removePostFromSavedList(String identifier, Authentication authentication) throws PostNotFoundException, UserNotFoundException;

    ResponsePostDTO addPostToSavedList(String identifier, Authentication authentication) throws PostNotFoundException, UserNotFoundException;

    boolean isPostExistInSavedList(String identifier, Authentication authentication) throws PostNotFoundException, UserNotFoundException;

    /**
     * Retrieves a paginated list of posts created by users that the authenticated user is following.
     *
     * @param authentication The authentication object for the current user.
     * @param pageNumber     The page number to retrieve.
     * @param pageSize       The number of posts to include on each page.
     * @param sortByValue    The field by which to sort the posts.
     * @return A map containing 50 posts, overall number of comments, current comment page, and overall number of pages.
     * @throws IllegalArgumentException If the user is not found.
     */
    Page<PostEntity> getPostsByUserFollowing(Authentication authentication, int pageNumber, int pageSize, String sortByValue) throws UserNotFoundException, InvalidPageSizeException;

    /**
     * Retrieves a paginated list of posts that contain the specified text in their title.
     *
     * @param title       The text to search for in the post titles.
     * @param pageNumber  The page number to retrieve.
     * @param pageSize    The number of posts to include on each page.
     * @param sortByValue The field by which to sort the posts.
     * @return A map containing 50 posts, overall number of comments, current comment page, and overall number of pages.
     */
    Page<PostEntity> findPostsByTextContaining(String title, int pageNumber, int pageSize, String sortByValue) throws InvalidPageSizeException;

    /**
     * Retrieves a post by its identifier.
     *
     * @param identifier The identifier of the post.
     * @return The DTO representing the post.
     */
    ResponsePostDTO getPostById(String identifier, HttpServletRequest request, Authentication authentication) throws PostNotFoundException, UserNotFoundException, UserStatisticsNotFoundException;

    ResponsePostDTO deletePostByIdentifier(String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException;

    Mono<Map<PostEntity, Set<UserEntity>>> getBatchedUpvotes(List<PostEntity> posts) throws UpvotesNotFoundException;

    Mono<Map<PostEntity, Set<UserEntity>>> getBatchedDownvotes(List<PostEntity> posts) throws DownvotesNotFoundException;


    PostEntity getPostById(String identifier);
}
