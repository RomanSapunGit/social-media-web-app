package com.roman.sapun.java.socialmedia.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.roman.sapun.java.socialmedia.dto.credentials.ValidatorDTO;
import com.roman.sapun.java.socialmedia.dto.user.ResponseUserDTO;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import org.springframework.security.core.Authentication;

public interface SubscriptionService {
    /**
     * Adds a user to the following list of the current user.
     *
     * @param authentication The authentication object of the current user.
     * @param username       The username of the user to follow.
     * @return The details of the user being followed.
     */
    ResponseUserDTO addFollowing(Authentication authentication, String username) throws JsonProcessingException, UserNotFoundException;

    /**
     * Retrieves information about a user's following relationship with another user identified by their username.
     *
     * @param authentication The authentication object representing the currently authenticated user.
     * @param username       The username of the user for whom the following relationship is to be checked.
     * @return A ValidatorDTO containing information about the following relationship:
     * - {@code valid}: Indicates whether the following relationship exists (true) or not (false).
     */
    ValidatorDTO findFollowingByUsername(Authentication authentication, String username) throws UserNotFoundException;

    /**
     * Checks if the current user has any subscriptions (followings).
     *
     * @param authentication The authentication object of the current user.
     * @return A validator object indicating if the user has subscriptions.
     */
    ValidatorDTO hasSubscriptions(Authentication authentication) throws UserNotFoundException;

    /**
     * Removes a user from the following list of the current user.
     *
     * @param authentication The authentication object of the current user.
     * @param username       The username of the user to unfollow.
     * @return The details of the user being unfollowed.
     */
    ResponseUserDTO removeFollowing(Authentication authentication, String username) throws UserNotFoundException;
}
