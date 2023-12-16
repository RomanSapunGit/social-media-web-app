package com.roman.sapun.java.socialmedia.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.roman.sapun.java.socialmedia.dto.credentials.ValidatorDTO;
import com.roman.sapun.java.socialmedia.dto.page.UserPageDTO;
import com.roman.sapun.java.socialmedia.dto.user.RequestUserDTO;
import com.roman.sapun.java.socialmedia.dto.user.ResponseUserDTO;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import com.roman.sapun.java.socialmedia.service.SubscriptionService;
import com.roman.sapun.java.socialmedia.service.UserService;
import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;

    @Autowired
    public UserController(UserService userService, SubscriptionService subscriptionService) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
    }

    /**
     * Retrieves a paginated list of users whose usernames contain the provided substring.
     *
     * @param username The substring to search for within usernames.
     * @param page     The page number for pagination.
     * @param pageSize The number of users to display per page (default is 5).
     * @param sortBy   The sorting criteria for the results (default is by username).
     * @return A map containing a paginated list of users, overall number of users, current user page, and overall number of pages.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{username}")
    @Cacheable(value = "userCache", key = "#username.toString() + #page+ '-' + #pageSize", unless = "#result == null")
    public UserPageDTO getUsersByUsernameContaining(@PathVariable String username, @RequestParam int page,
                                                    @RequestParam(defaultValue = "5") int pageSize,
                                                    @RequestParam(defaultValue = "username") String sortBy) throws UserNotFoundException {
        return userService.getUsersByUsernameContaining(username, page, pageSize, sortBy);
    }

    /**
     * Updates user information based on the provided RequestUserDTO.
     *
     * @param requestUserDTO The RequestUserDTO containing updated user details.
     * @param authentication The authentication object representing the currently logged-in user.
     * @return The updated RequestUserDTO with the modified user details.
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping()
    public RequestUserDTO updateUser(@RequestBody RequestUserDTO requestUserDTO, Authentication authentication) throws UserNotFoundException {
        return userService.updateUser(requestUserDTO, authentication);
    }

    /**
     * Retrieves a paginated list of users sorted by specific criteria.
     *
     * @param page     The page number for pagination.
     * @param pageSize The number of users to display per page (default is 5).
     * @return A list containing users, overall number of users, current user page, and overall number of pages.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    @Cacheable(value = "userCache", key = "#page + #page + '-' + #pageSize", unless = "#result == null")
    public UserPageDTO getUsers(@RequestParam int page, @RequestParam(defaultValue = "5") int pageSize) throws UserNotFoundException {
        return userService.getUsers(page, pageSize);
    }

    /**
     * Adds a user to the following list of the currently authenticated user.
     *
     * @param username       The username containing the username value to follow.
     * @param authentication The authentication object representing the currently logged-in user.
     * @return The ResponseUserDTO indicating successful following.
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/follower")
    public ResponseUserDTO addFollowing(@RequestBody String username, Authentication authentication) throws JsonProcessingException, UserNotFoundException {
        return subscriptionService.addFollowing(authentication, username);
    }

    /**
     * Checks if the currently authenticated user has any followers.
     *
     * @param authentication The authentication object representing the currently logged-in user.
     * @return The ValidatorDTO indicating whether the user has subscriptions (followers) or not.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/follower")
    public ValidatorDTO hasSubscriptions(Authentication authentication) throws UserNotFoundException {
        return subscriptionService.hasSubscriptions(authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/follower/{username}")
    public ValidatorDTO findFollowingByUsername(Authentication authentication, @PathVariable String username) throws UserNotFoundException {
        return subscriptionService.findFollowingByUsername(authentication, username);
    }

    /**
     * Removes a user from the following list of the currently authenticated user.
     *
     * @param username       The username of the user to unfollow.
     * @param authentication The authentication object representing the currently logged-in user.
     * @return The ResponseUserDTO indicating successful unfollowing.
     */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/follower/{username}")
    public ResponseUserDTO removeFollowing(@PathVariable String username, Authentication authentication) throws UserNotFoundException {
        return subscriptionService.removeFollowing(authentication, username);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/current")
    public ResponseUserDTO getCurrentUser(Authentication authentication) throws UserNotFoundException {
        return userService.getCurrentUser(authentication);
    }
    /**
     * Blocks a user based on the provided username (only accessible by ADMIN role).
     *
     * @param username The username of the user to be blocked.
     * @return The ResponseUserDTO indicating the blocked user.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{username}")
    public ResponseUserDTO blockUser(@PathVariable String username) throws UserNotFoundException {
        return userService.blockUser(username);
    }

    /**
     * Unblocks a user based on the provided username (only accessible by ADMIN role).
     *
     * @param username The username of the user to be unlocked.
     * @return The ResponseUserDTO indicating the unlocked user.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{username}")
    public ResponseUserDTO unlockUser(@PathVariable String username) throws UserNotFoundException {
        return userService.unlockUser(username);
    }
}
