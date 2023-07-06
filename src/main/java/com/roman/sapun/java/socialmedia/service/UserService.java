package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.UserDTO;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface UserService {
    Map<String, Object> getUsersByUsername(String regex, int page);

    UserDTO updateUser(UserDTO userDTO, Authentication authentication);

    UserDTO blockUser(String username);

    UserDTO unlockUser(String username);

    UserEntity findUserByAuth(Authentication authentication);

}
