package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.comment.CommentDTO;
import com.roman.sapun.java.socialmedia.dto.comment.RequestCommentDTO;
import com.roman.sapun.java.socialmedia.dto.comment.ResponseCommentDTO;
import com.roman.sapun.java.socialmedia.dto.page.CommentPageDTO;
import com.roman.sapun.java.socialmedia.exception.CommentNotFoundException;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import com.roman.sapun.java.socialmedia.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public CommentDTO createComment(@RequestBody RequestCommentDTO requestCommentDTO, Authentication authentication) throws UserNotFoundException, CommentNotFoundException {
        return commentService.createComment(requestCommentDTO, authentication);
    }

    /**
     * Deletes a comment by its ID.
     *
     * @param id              The ID of the comment to be deleted.
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
     * @param postId   The ID of the post.
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
}