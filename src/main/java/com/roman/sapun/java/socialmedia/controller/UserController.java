package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.UserDTO;
import com.roman.sapun.java.socialmedia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @GetMapping("{username}")
    public Map<String, Object> getUsersByUsername(@PathVariable String username, @RequestParam int page) {
        return userService.getUsersByUsername(username, page);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping()
    public UserDTO updateUser(@RequestBody UserDTO userDTO, Authentication authentication) {
        return userService.updateUser(userDTO, authentication);
    }
}
