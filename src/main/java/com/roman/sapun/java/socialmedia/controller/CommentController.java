package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.comment.CommentDTO;
import com.roman.sapun.java.socialmedia.dto.comment.RequestCommentDTO;
import com.roman.sapun.java.socialmedia.dto.comment.ResponseCommentDTO;
import com.roman.sapun.java.socialmedia.dto.page.CommentPageDTO;
import com.roman.sapun.java.socialmedia.exception.*;
import com.roman.sapun.java.socialmedia.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
/**
 * CommentController is a Spring REST controller responsible for managing comment-related operations.
 * It includes endpoints for creating, deleting, updating, and retrieving comments, as well as handling
 * saved comments.
 */
@RestController
@RequestMapping("/api/v1/comment")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {

        this.commentService = commentService;
    }


    /**
     * Creates a new comment.
     *
     * @param requestCommentDTO The DTO containing the details of the new comment.
     * @param authentication    The authentication object representing the current user.
     * @param request           The HttpServletRequest object.
     * @return The DTO representing the created comment.
     * @throws UserNotFoundException           If the user is not found.
     * @throws CommentNotFoundException        If the comment is not found.
     * @throws UserStatisticsNotFoundException If user statistics are not found.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CommentDTO createComment(@RequestBody RequestCommentDTO requestCommentDTO,
                                    Authentication authentication,
                                    HttpServletRequest request) throws UserNotFoundException, CommentNotFoundException, UserStatisticsNotFoundException {
        return commentService.createComment(requestCommentDTO, authentication, request);
    }

    /**
     * Deletes a comment by its identifier.
     *
     * @param id             The identifier of the comment to be deleted.
     * @param authentication The authentication object representing the current user.
     * @return The DTO representing the deleted comment.
     * @throws CommentNotFoundException If the comment with the specified ID is not found.
     * @throws UserNotFoundException    If the user is not found.
     */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public ResponseCommentDTO deleteComment(@PathVariable String id, Authentication authentication) throws CommentNotFoundException, UserNotFoundException {
        return commentService.deleteComment(id, authentication);
    }


    /**
     * Updates a comment by its identifier.
     *
     * @param requestCommentDTO The DTO containing the updated details of the comment.
     * @param id                The identifier of the comment to be updated.
     * @param authentication    The authentication object representing the current user.
     * @return The DTO representing the updated comment.
     * @throws CommentNotFoundException If the comment with the specified ID is not found or Authors and User's data does not match.
     * @throws UserNotFoundException    If the user is not found.
     */
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}")
    public ResponseCommentDTO updateCommentById(@RequestBody RequestCommentDTO requestCommentDTO, @PathVariable String id,
                                                Authentication authentication) throws CommentNotFoundException, UserNotFoundException {
        return commentService.updateCommentById(requestCommentDTO, id, authentication);
    }

    /**
     * Retrieves saved comments for the authenticated user.
     *
     * @param authentication The authentication object representing the current user.
     * @param pageNumber     The page number for pagination.
     * @return A CommentPageDTO containing saved comments, the overall number of comments, current comment page, and overall number of pages.
     * @throws UserNotFoundException    If the user is not found.
     * @throws InvalidPageSizeException If the page size is invalid.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/saved")
    public CommentPageDTO getSavedComments(Authentication authentication, @RequestParam int pageNumber) throws UserNotFoundException, InvalidPageSizeException {
        return commentService.getSavedComments(authentication, pageNumber);
    }

    /**
     * Checks if a comment with the specified identifier is saved by the authenticated user.
     *
     * @param identifier      The identifier of the comment.
     * @param authentication  The authentication object representing the current user.
     * @return True if the comment is saved; otherwise, false.
     * @throws UserNotFoundException If the user is not found.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/saved/{identifier}")
    public boolean findSavedCommentByIdentifier(@PathVariable String identifier, Authentication authentication) throws UserNotFoundException {
        return commentService.findSavedCommentByIdentifier(identifier, authentication);
    }

    /**
     * Deletes a saved comment for the authenticated user.
     *
     * @param identifier      The identifier of the comment to be removed from the saved list.
     * @param authentication  The authentication object representing the current user.
     * @return The DTO representing the removed comment.
     * @throws CommentNotFoundException If the comment with the specified identifier is not found.
     * @throws UserNotFoundException    If the user is not found.
     */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/saved/{identifier}")
    public ResponseCommentDTO deleteSavedComment(@PathVariable String identifier, Authentication authentication) throws CommentNotFoundException, UserNotFoundException {
        return commentService.removeCommentFromSavedList(identifier, authentication);
    }

    /**
     * Saves a comment for the authenticated user.
     *
     * @param identifier      The identifier of the comment to be saved.
     * @param authentication  The authentication object representing the current user.
     * @return The DTO representing the saved comment.
     * @throws UserNotFoundException If the user is not found.
     * @throws CommentNotFoundException If the comment with the specified identifier is not found.
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/saved/{identifier}")
    public ResponseCommentDTO saveComment(@PathVariable String identifier, Authentication authentication) throws UserNotFoundException, CommentNotFoundException {
        return commentService.addCommentToSavedList(identifier, authentication);
    }
}