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
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comment")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {

        this.commentService = commentService;
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CommentDTO createComment(@RequestBody RequestCommentDTO requestCommentDTO, Authentication authentication, HttpServletRequest request) throws UserNotFoundException, CommentNotFoundException, UserStatisticsNotFoundException {
        return commentService.createComment(requestCommentDTO, authentication, request);
    }

    /**
     * Deletes a comment by its ID.
     *
     * @param id             The ID of the comment to be deleted.
     * @param authentication The authentication object representing the current user.
     * @return The DTO representing the deleted comment.
     * @throws CommentNotFoundException If the comment with the specified ID is not found.
     */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public ResponseCommentDTO deleteComment(@PathVariable String id, Authentication authentication) throws CommentNotFoundException, UserNotFoundException {
        return commentService.deleteComment(id, authentication);
    }

    /**
     * Retrieves comments for a post identified by its ID and caches the results for 30 minutes.
     *
     * @param postId     The ID of the post.
     * @param pageNumber The page number for pagination.
     * @return map that includes 50 comments, overall number of comments, current comment page and overall number of pages.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{postId}")
    public CommentPageDTO getCommentsByPostIdentifier(@PathVariable String postId, @RequestParam int pageNumber) throws CommentNotFoundException {
        return commentService.getCommentsByPostIdentifier(postId, pageNumber);
    }

    /**
     * Updates a comment by its ID.
     *
     * @param requestCommentDTO The DTO containing the updated details of the comment.
     * @param id                The ID of the comment to be updated.
     * @param authentication    The authentication object representing the current user.
     * @return The DTO representing the updated comment.
     * @throws CommentNotFoundException If the comment with the specified ID is not found or Authors and User's data does not match.
     */
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}")
    public ResponseCommentDTO updateCommentById(@RequestBody RequestCommentDTO requestCommentDTO, @PathVariable String id,
                                                Authentication authentication) throws CommentNotFoundException, UserNotFoundException {
        return commentService.updateCommentById(requestCommentDTO, id, authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/saved")
    public CommentPageDTO getSavedComments(Authentication authentication, @RequestParam int pageNumber) throws UserNotFoundException, InvalidPageSizeException {
        return commentService.getSavedComments(authentication, pageNumber);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/saved/{identifier}")
    public boolean findSavedCommentByIdentifier(@PathVariable String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException {
        return commentService.findSavedCommentByIdentifier(identifier, authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/saved/{identifier}")
    public ResponseCommentDTO deleteSavedComment(@PathVariable String identifier, Authentication authentication) throws CommentNotFoundException, UserNotFoundException {
        return commentService.removeCommentFromSavedList(identifier, authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/saved/{identifier}")
    public ResponseCommentDTO saveComment(@PathVariable String identifier, Authentication authentication) throws UserNotFoundException, CommentNotFoundException {
        return commentService.addCommentToSavedList(identifier, authentication);
    }
}