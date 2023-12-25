package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.page.UserPageDTO;
import com.roman.sapun.java.socialmedia.dto.user.ConsentDTO;
import com.roman.sapun.java.socialmedia.dto.user.RequestUserDTO;
import com.roman.sapun.java.socialmedia.dto.user.ResponseUserDTO;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import org.springframework.security.core.Authentication;

public interface UserService {
    /**
     * Retrieves a paginated list of users whose usernames contain the specified text.
     *
     * @param username    The text to search for within usernames.
     * @param pageNumber  The page number for pagination.
     * @param pageSize    The number of users to display per page.
     * @param sortByValue The criteria for sorting the user results (e.g., by username).
     * @return A map containing a paginated list of users matching the search criteria, along with
     * information about the overall number of users, the current page, and the total number of pages.
     */
    UserPageDTO getUsersByUsernameContaining(String username, int pageNumber, int pageSize, String sortByValue) throws UserNotFoundException;

    /**
     * Retrieves a paginated list of all users.
     *
     * @param page     The page number for pagination.
     * @param pageSize The number of users to display per page.
     * @return A list containing users on the specified page, typically up to the specified page size.
     */
    UserPageDTO getUsers(int page, int pageSize) throws UserNotFoundException;

    /**
     * Updates the details of the current user.
     *
     * @param requestUserDTO The updated user details.
     * @param authentication The authentication object of the current user.
     * @return The updated user details.
     */
    RequestUserDTO updateUser(RequestUserDTO requestUserDTO, Authentication authentication) throws UserNotFoundException;

    /**
     * Blocks a user with the specified username.
     *
     * @param username The username of the user to block.
     * @return The blocked user details.
     */
    ResponseUserDTO blockUser(String username) throws UserNotFoundException;

    /**
     * Unlocks a user with the specified username.
     *
     * @param username The username of the user to unlock.
     * @return The unlocked user details.
     */
    ResponseUserDTO unlockUser(String username) throws UserNotFoundException;


    ConsentDTO sendUserConsent(Authentication authentication, ConsentDTO consent) throws Exception;

    ConsentDTO getConsent(Authentication authentication) throws UserNotFoundException;

    ResponseUserDTO getCurrentUser(Authentication authentication) throws UserNotFoundException;

    /**
     * Finds the user entity based on the authentication object.
     *
     * @param authentication The authentication object of the user.
     * @return The user entity.
     */
    UserEntity findUserByAuth(Authentication authentication) throws UserNotFoundException;
}
