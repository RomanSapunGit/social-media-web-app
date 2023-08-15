package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.credentials.ValidatorDTO;
import com.roman.sapun.java.socialmedia.dto.user.RequestUserDTO;
import com.roman.sapun.java.socialmedia.dto.user.ResponseUserDTO;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface UserService {
    /**
     * Retrieves a paginated list of users whose usernames contain the specified text.
     *
     * @param username   The text to search for in usernames.
     * @param pageNumber The page number to retrieve.
     * @return map containing 50 Users, overall number of comments, current comment page and overall number of pages.
     */
    Map<String, Object> getUsersByUsernameContaining(String username, int pageNumber);
    /**
     * Retrieves a paginated list of all users.
     *
     * @param page The page number to retrieve.
     * @return map containing 50 Users, overall number of comments, current comment page and overall number of pages.
     */
    Map<String, Object> getUsers(int page);
    /**
     * Updates the details of the current user.
     *
     * @param requestUserDTO   The updated user details.
     * @param authentication   The authentication object of the current user.
     * @return The updated user details.
     */
    RequestUserDTO updateUser(RequestUserDTO requestUserDTO, Authentication authentication);
    /**
     * Blocks a user with the specified username.
     *
     * @param username The username of the user to block.
     * @return The blocked user details.
     */
    ResponseUserDTO blockUser(String username);
    /**
     * Unlocks a user with the specified username.
     *
     * @param username The username of the user to unlock.
     * @return The unlocked user details.
     */
    ResponseUserDTO unlockUser(String username);
    /**
     * Adds a user to the following list of the current user.
     *
     * @param authentication The authentication object of the current user.
     * @param username       The username of the user to follow.
     * @return The details of the user being followed.
     */
    ResponseUserDTO addFollowing(Authentication authentication, String username);
    /**
     * Checks if the current user has any subscriptions (followings).
     *
     * @param authentication The authentication object of the current user.
     * @return A validator object indicating if the user has subscriptions.
     */
    ValidatorDTO hasSubscriptions(Authentication authentication);
    /**
     * Removes a user from the following list of the current user.
     *
     * @param authentication The authentication object of the current user.
     * @param username       The username of the user to unfollow.
     * @return The details of the user being unfollowed.
     */
    ResponseUserDTO removeFollowing(Authentication authentication, String username);
    /**
     * Finds the user entity based on the authentication object.
     *
     * @param authentication The authentication object of the user.
     * @return The user entity.
     */
    UserEntity findUserByAuth(Authentication authentication);

}
