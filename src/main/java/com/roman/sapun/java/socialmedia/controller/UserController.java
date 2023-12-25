package com.roman.sapun.java.socialmedia.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.roman.sapun.java.socialmedia.dto.credentials.ValidatorDTO;
import com.roman.sapun.java.socialmedia.dto.page.UserPageDTO;
import com.roman.sapun.java.socialmedia.dto.user.ConsentDTO;
import com.roman.sapun.java.socialmedia.dto.user.RequestUserDTO;
import com.roman.sapun.java.socialmedia.dto.user.ResponseUserDTO;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import com.roman.sapun.java.socialmedia.service.SubscriptionService;
import com.roman.sapun.java.socialmedia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
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
     * @throws UserNotFoundException If no users are found with the specified substring.
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
     * @throws UserNotFoundException If the user is not found.
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
     * @throws UserNotFoundException If no users are found.
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
     * @throws JsonProcessingException If there is an issue with processing the JSON request body.
     * @throws UserNotFoundException   If the specified user is not found.
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/follower")
    public ResponseUserDTO addFollowing(@RequestBody String username, Authentication authentication) throws JsonProcessingException, UserNotFoundException {
        return subscriptionService.addFollowing(authentication, username);
    }

    /**
     * Sends user consent based on the provided ConsentDTO.
     *
     * @param authentication The authentication object representing the currently logged-in user.
     * @param consent         The ConsentDTO containing the consent details.
     * @return The ConsentDTO indicating successful consent.
     * @throws Exception If there is an issue with processing the consent.
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/consent")
    public ConsentDTO sendUserConsent(Authentication authentication, @RequestBody ConsentDTO consent) throws Exception {
        return userService.sendUserConsent(authentication, consent);
    }

    /**
     * Retrieves user consent details.
     *
     * @param authentication The authentication object representing the currently logged-in user.
     * @return The ConsentDTO containing the user's consent details.
     * @throws UserNotFoundException If the user is not found.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/consent")
    public ConsentDTO getUserConsent(Authentication authentication) throws UserNotFoundException {
        return userService.getConsent(authentication);
    }

    /**
     * Checks if the currently authenticated user has any followers.
     *
     * @param authentication The authentication object representing the currently logged-in user.
     * @return The ValidatorDTO indicating whether the user has subscriptions (followers) or not.
     * @throws UserNotFoundException If the user is not found.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/follower")
    public ValidatorDTO hasSubscriptions(Authentication authentication) throws UserNotFoundException {
        return subscriptionService.hasSubscriptions(authentication);
    }

    /**
     * Retrieves information about a user being followed by the currently authenticated user.
     *
     * @param authentication The authentication object representing the currently logged-in user.
     * @param username       The username of the user being followed.
     * @return The ValidatorDTO indicating whether the user is being followed or not.
     * @throws UserNotFoundException If the specified user is not found.
     */
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
     * @throws UserNotFoundException If the specified user is not found.
     */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/follower/{username}")
    public ResponseUserDTO removeFollowing(@PathVariable String username, Authentication authentication) throws UserNotFoundException {
        return subscriptionService.removeFollowing(authentication, username);
    }

    /**
     * Retrieves information about the currently authenticated user.
     *
     * @param authentication The authentication object representing the currently logged-in user.
     * @return The ResponseUserDTO containing details about the authenticated user.
     * @throws UserNotFoundException If the authenticated user is not found.
     */
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
     * @throws UserNotFoundException If the specified user is not found.
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
     * @throws UserNotFoundException If the specified user is not found.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{username}")
    public ResponseUserDTO unlockUser(@PathVariable String username) throws UserNotFoundException {
        return userService.unlockUser(username);
    }
}
