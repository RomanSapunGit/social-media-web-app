package com.roman.sapun.java.socialmedia.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.roman.sapun.java.socialmedia.dto.credentials.ValidatorDTO;
import com.roman.sapun.java.socialmedia.dto.post.RequestPostDTO;
import com.roman.sapun.java.socialmedia.dto.post.ResponsePostDTO;
import com.roman.sapun.java.socialmedia.dto.user.ResponseUserDTO;
import com.roman.sapun.java.socialmedia.exception.InvalidPageSizeException;
import com.roman.sapun.java.socialmedia.exception.PostNotFoundException;
import com.roman.sapun.java.socialmedia.exception.TagNotFoundException;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import com.roman.sapun.java.socialmedia.service.PostService;
import com.roman.sapun.java.socialmedia.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/post")
public class PostController {

    private final PostService postService;
    private final VoteService voteService;

    @Autowired
    public PostController(PostService postService, VoteService voteService) {
        this.postService = postService;
        this.voteService = voteService;
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
                                      @RequestPart("images") List<MultipartFile> images, Authentication authentication) throws UserNotFoundException {
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
                                      Authentication authentication) throws PostNotFoundException, UserNotFoundException {
        return postService.updatePost(requestPostDTO, images, authentication);
    }

    /**
     * Retrieves a list of posts containing the specified text in their titles and specifying posts by sorting criteria.
     *
     * @param title     The text to search for in post titles.
     * @param page      The page number of the results.
     * @param pageSize  The number of posts to display per page (default is 20).
     * @param sortBy    The sorting criteria for the results (default is 'creationTime').
     * @return A map containing posts, total post count, current page, and total pages.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search/{title}")
    @Cacheable(value = "postCache", key = "#title.toString() + '-' + #page + '-' + #pageSize + '-' + #sortBy",
            unless = "#result.get('total') == 0")
    public Map<String, Object> getPostsByTextContaining(@PathVariable String title, @RequestParam int page,
                                                        @RequestParam(defaultValue = "20") int pageSize,
                                                        @RequestParam(defaultValue = "creationTime") String sortBy) throws InvalidPageSizeException {
        return postService.findPostsByTextContaining(title, page, pageSize, sortBy);
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
    public Map<String, Object> getPostsByTag(@PathVariable String tag, @RequestParam int page,
                                             @RequestParam(defaultValue = "5") int pageSize,
                                             @RequestParam(defaultValue = "creationTime") String sortBy) throws InvalidPageSizeException, TagNotFoundException {
        return postService.getPostsByTag(tag, page, pageSize, sortBy);
    }

    /**
     * Retrieves posts with sorting criteria created by a specific user.
     *
     * @param username The username of the user.
     * @param page     The page number of the results.
     * @param pageSize The number of posts to display per page (default is 5).
     * @param sortBy   The sorting criteria for the results (default is 'creationTime').
     * @return A map containing posts created by the specified user on the requested page.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/author/{username}")
    public Map<String, Object> getPostsByUsername(@PathVariable String username, @RequestParam int page,
                                                  @RequestParam(defaultValue = "5") int pageSize,
                                                  @RequestParam(defaultValue = "creationTime") String sortBy) throws InvalidPageSizeException, UserNotFoundException {
        return postService.getPostsByUsername(username, page, pageSize, sortBy);
    }

    /**
     * Retrieves posts with sorting criteria followed by the authenticated user.
     *
     * @param authentication The authentication object of the authenticated user.
     * @param page           The page number of the results.
     * @param pageSize       The number of posts to display per page (default is 50).
     * @param sortBy         The sorting criteria for the results (default is 'creationTime').
     * @return A map containing posts followed by the authenticated user on the requested page.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/follower")
    public Map<String, Object> getPostsBySubscription(Authentication authentication, @RequestParam int page,
                                                      @RequestParam(defaultValue = "50") int pageSize,
                                                      @RequestParam(defaultValue = "creationTime") String sortBy) throws UserNotFoundException, InvalidPageSizeException {
        return postService.getPostsByUserFollowing(authentication, page, pageSize, sortBy);
    }

    /**
     * Retrieves a paginated list of all posts with sorting criteria.
     *
     * @param page     The page number of the results.
     * @param pageSize The number of posts to display per page (default is 15).
     * @param sortBy   The sorting criteria for the results (default is 'creationTime').
     * @return A map containing a paginated list of all posts for the specified page.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search")
    @Cacheable(value = "postCache", key = "#page.toString() + '-' + #pageSize.toString() + '-' + #sortBy.toString()", unless = "#result.get('total') == 0")
    public Map<String, Object> getPosts(@RequestParam int page, @RequestParam(defaultValue = "15") int pageSize,
                                        @RequestParam(defaultValue = "creationTime") String sortBy) throws InvalidPageSizeException {
        return postService.getPosts(page, pageSize, sortBy);
    }

    /**
     * Retrieves a specific post by its identifier.
     *
     * @param identifier The identifier of the post.
     * @return The PostDTO object representing the specified post.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{identifier}")
    public ResponsePostDTO getPostById(@PathVariable String identifier) throws PostNotFoundException, UserNotFoundException {
        return postService.getPostById(identifier);
    }
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{identifier}")
    public ResponsePostDTO deletePost(@PathVariable String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException {
       return postService.deletePostByIdentifier(identifier, authentication);
    }
    /**
     * Adds an upvote to the specified post and returns the users who upvoted the post.
     *
     * @param identifier      The identifier of the post to upvote.
     * @param authentication  The authentication object of the user performing the upvote.
     * @return A set of users who upvoted the post.
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/upvote")
    public Set<ResponseUserDTO> upvotePost(@RequestBody String identifier, Authentication authentication) throws JsonProcessingException, UserNotFoundException, PostNotFoundException {
        return voteService.addUpvote(identifier, authentication);
    }
    /**
     * Adds a downvote to the specified post and returns the users who downvoted the post.
     *
     * @param identifier      The identifier of the post to downvote.
     * @param authentication  The authentication object of the user performing the downvote.
     * @return A set of users who downvoted the post.
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/downvote")
    public Set<ResponseUserDTO> downvotePost(@RequestBody @NonNull String identifier, Authentication authentication) throws JsonProcessingException, UserNotFoundException, PostNotFoundException {
        return voteService.addDownvote(identifier, authentication);
    }

    /**
     * Removes an upvote from the specified post and returns the updated list of upvoters.
     *
     * @param identifier      The identifier of the post to remove the upvote from.
     * @param authentication  The authentication object of the user performing the removal.
     * @return The updated set of users who upvoted the post.
     */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/upvote/{identifier}")
    public Set<ResponseUserDTO> removeUpvote(@PathVariable @NonNull String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException {
        return voteService.removeUpvote(identifier, authentication);
    }

    /**
     * Removes a downvote from the specified post and returns the updated list of downvoters.
     *
     * @param identifier      The identifier of the post to remove the downvote from.
     * @param authentication  The authentication object of the user performing the removal.
     * @return The updated set of users who downvoted the post.
     */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/downvote/{identifier}")
    public Set<ResponseUserDTO> removeDownvote(@PathVariable @NonNull String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException {
        return voteService.removeDownvote(identifier, authentication);
    }

    /**
     * Checks if the specified post has been upvoted by the authenticated user.
     *
     * @param identifier      The identifier of the post to check for upvotes.
     * @param authentication  The authentication object of the user performing the check.
     * @return A validation object indicating whether the post has been upvoted by the user.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/upvote/{identifier}")
    public ValidatorDTO isPostUpvoted(@PathVariable @NonNull String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException {
        return voteService.isUpvoteMade(identifier, authentication);
    }

    /**
     * Checks if the specified post has been downvoted by the authenticated user.
     *
     * @param identifier      The identifier of the post to check for downvotes.
     * @param authentication  The authentication object of the user performing the check.
     * @return A validation object indicating whether the post has been downvoted by the user.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/downvote/{identifier}")
    public ValidatorDTO isPostDownvoted(@PathVariable @NonNull String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException {
        return voteService.isDownvoteMade(identifier, authentication);
    }
}