package com.roman.sapun.java.socialmedia.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.roman.sapun.java.socialmedia.dto.VoteDTO;
import com.roman.sapun.java.socialmedia.dto.credentials.ValidatorDTO;
import com.roman.sapun.java.socialmedia.exception.PostNotFoundException;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface VoteService {
    /**
     * Add an upvote to a post identified by its identifier.
     *
     * @param identifier     The identifier of the post to upvote.
     * @param authentication The authentication object of the user performing the upvote.
     * @return A set of user DTOs indicating users who upvoted the post.
     * @throws JsonProcessingException If there is an issue processing the JSON data.
     */
        List<VoteDTO> addUpvote(String identifier, Authentication authentication) throws JsonProcessingException, UserNotFoundException, PostNotFoundException;

        /**
         * Remove an upvote from a post identified by its identifier.
         *
         * @param identifier     The identifier of the post to remove the upvote from.
         * @param authentication The authentication object of the user removing the upvote.
         * @return A set of user DTOs indicating users who upvoted the post after the removal.
         */
        List<VoteDTO> removeUpvote(String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException;

        /**
         * Add a downvote to a post identified by its identifier.
         *
         * @param identifier     The identifier of the post to downvote.
         * @param authentication The authentication object of the user performing the downvote.
         * @return A set of user DTOs indicating users who downvoted the post.
         * @throws JsonProcessingException If there is an issue processing the JSON data.
         */
        List<VoteDTO> addDownvote(String identifier, Authentication authentication) throws JsonProcessingException, UserNotFoundException, PostNotFoundException;

        /**
         * Remove a downvote from a post identified by its identifier.
         *
         * @param identifier     The identifier of the post to remove the downvote from.
         * @param authentication The authentication object of the user removing the downvote.
         * @return A set of user DTOs indicating users who downvoted the post after the removal.
         */
        List<VoteDTO> removeDownvote(String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException;

        /**
         * Check if an upvote is made by the authenticated user for a post identified by its identifier.
         *
         * @param identifier      The identifier of the post to check for upvote.
         * @param authentication  The authentication object of the user.
         * @return A validator DTO indicating whether the user made an upvote.
         */
        ValidatorDTO isUpvoteMade(String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException;

        /**
         * Check if a downvote is made by the authenticated user for a post identified by its identifier.
         *
         * @param identifier      The identifier of the post to check for downvote.
         * @param authentication  The authentication object of the user.
         * @return A validator DTO indicating whether the user made a downvote.
         */
        ValidatorDTO isDownvoteMade(String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException;
        }
