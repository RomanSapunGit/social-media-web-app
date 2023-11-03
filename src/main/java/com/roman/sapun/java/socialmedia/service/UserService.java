package com.roman.sapun.java.socialmedia.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.roman.sapun.java.socialmedia.dto.credentials.ValidatorDTO;
import com.roman.sapun.java.socialmedia.dto.user.RequestUserDTO;
import com.roman.sapun.java.socialmedia.dto.user.ResponseUserDTO;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

public interface UserService {
    /**
     * Retrieves a paginated list of users whose usernames contain the specified text.
     *
     * @param username    The text to search for within usernames.
     * @param pageNumber  The page number for pagination.
     * @param pageSize    The number of users to display per page.
     * @param sortByValue The criteria for sorting the user results (e.g., by username).
     *
     * @return A map containing a paginated list of users matching the search criteria, along with
     *         information about the overall number of users, the current page, and the total number of pages.
     */
    Map<String, Object> getUsersByUsernameContaining(String username, int pageNumber, int pageSize, String sortByValue);

    /**
     * Retrieves a paginated list of all users.
     *
     * @param page     The page number for pagination.
     * @param pageSize The number of users to display per page.
     *
     * @return A list containing users on the specified page, typically up to the specified page size.
     */
    List<ResponseUserDTO> getUsers(int page, int pageSize);

    /**
     * Updates the details of the current user.
     *
     * @param requestUserDTO The updated user details.
     * @param authentication The authentication object of the current user.
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
     * Finds the user entity based on the authentication object.
     *
     * @param authentication The authentication object of the user.
     * @return The user entity.
     */
    UserEntity findUserByAuth(Authentication authentication);
}
