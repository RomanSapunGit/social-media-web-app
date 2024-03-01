package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.page.UserPageDTO;
import com.roman.sapun.java.socialmedia.dto.user.ConsentDTO;
import com.roman.sapun.java.socialmedia.dto.user.RequestUserDTO;
import com.roman.sapun.java.socialmedia.dto.user.ResponseUserDTO;
import com.roman.sapun.java.socialmedia.entity.CommentEntity;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.exception.*;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface UserService {
    /**
     * Retrieves a paginated list of users whose usernames contain the specified text.
     *
     * @param username    The text to search for within usernames.
     * @param page  The page number for pagination.
     * @param pageSize    The number of users to display per page.
     * @return A map containing a paginated list of users matching the search criteria, along with
     * information about the overall number of users, the current page, and the total number of pages.
     */
    Page<UserEntity> getUsersByUsernameContaining(String username, int pageSize, int page);

    UserPageDTO getUsersByUsernameContaining(String username, int page, int pageSize, String sortBy) throws UserNotFoundException;

    /**
     * Retrieves a paginated list of all users.
     *
     * @param page     The page number for pagination.
     * @param pageSize The number of users to display per page.
     * @return A list containing users on the specified page, typically up to the specified page size.
     */
    Page<UserEntity> getUsers(int page, int pageSize);

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

    Mono<Map<PostEntity, UserEntity>> getBatchedAuthorsForPosts(List<PostEntity> posts) throws UserNotFoundException, PostNotFoundException;
    Mono<Map<CommentEntity, UserEntity>> getBatchedAuthorsForComments(List<CommentEntity> commentEntities) throws CommentNotFoundException;

    ResponseUserDTO getCurrentUser(Authentication authentication) throws UserNotFoundException;

    /**
     * Finds the user entity based on the authentication object.
     *
     * @param authentication The authentication object of the user.
     * @return The user entity.
     */
    UserEntity findUserByAuth(Authentication authentication) throws UserNotFoundException;
}
