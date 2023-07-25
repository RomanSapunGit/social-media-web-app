package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.user.RequestUserDTO;
import com.roman.sapun.java.socialmedia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{username}")
    public Map<String, Object> getUserByUsername(@PathVariable String username, @RequestParam int page) {
        return userService.getUsersByUsernameContaining(username, page);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping()
    public RequestUserDTO updateUser(@RequestBody RequestUserDTO requestUserDTO, Authentication authentication) {
        return userService.updateUser(requestUserDTO, authentication);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    public Map<String, Object> getUsers(@RequestParam int page) {
        return  userService.getUsers(page);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{username}")
    public RequestUserDTO blockUser(@PathVariable String username) {
        return userService.blockUser(username);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{username}")
    public RequestUserDTO unlockUser(@PathVariable String username) {
        return userService.unlockUser(username);
    }
}
