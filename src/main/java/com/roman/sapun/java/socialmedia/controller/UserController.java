package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.credentials.ValidatorDTO;
import com.roman.sapun.java.socialmedia.dto.user.RequestUserDTO;
import com.roman.sapun.java.socialmedia.dto.user.ResponseUserDTO;
import com.roman.sapun.java.socialmedia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves a paginated list of users by searching for usernames containing the provided substring.
     *
     * @param username The substring to search for within usernames.
     * @param page     The page number for pagination.
     * @return map that includes 50 users, overall number of comments, current comment page and overall number of pages.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{username}")
    public Map<String, Object> getUserByUsername(@PathVariable String username, @RequestParam int page) {
        return userService.getUsersByUsernameContaining(username, page);
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
    public RequestUserDTO updateUser(@RequestBody RequestUserDTO requestUserDTO, Authentication authentication) {
        return userService.updateUser(requestUserDTO, authentication);
    }

    /**
     * Retrieves a paginated list of users.
     *
     * @param page The page number for pagination.
     * @return map that includes 50 users, overall number of comments, current comment page and overall number of pages.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    public Map<String, Object> getUsers(@RequestParam int page) {
        return userService.getUsers(page);
    }

    /**
     * Adds a user to the following list of the currently authenticated user.
     *
     * @param requestUserDTO The RequestUserDTO containing the username to follow.
     * @param authentication The authentication object representing the currently logged-in user.
     * @return The ResponseUserDTO indicating successful following.
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/follower")
    public ResponseUserDTO addFollowing(@RequestBody RequestUserDTO requestUserDTO, Authentication authentication) {
        return userService.addFollowing(authentication, requestUserDTO.username());
    }

    /**
     * Checks if the currently authenticated user has any followers.
     *
     * @param authentication The authentication object representing the currently logged-in user.
     * @return The ValidatorDTO indicating whether the user has subscriptions (followers) or not.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/follower")
    public ValidatorDTO hasSubscriptions(Authentication authentication) {
        return userService.hasSubscriptions(authentication);
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
    public ResponseUserDTO removeFollowing(@PathVariable String username, Authentication authentication) {
        return userService.removeFollowing(authentication, username);
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
    public ResponseUserDTO blockUser(@PathVariable String username) {
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
    public ResponseUserDTO unlockUser(@PathVariable String username) {
        return userService.unlockUser(username);
    }
}
