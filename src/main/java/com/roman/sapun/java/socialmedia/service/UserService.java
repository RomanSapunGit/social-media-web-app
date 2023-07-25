package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.user.RequestUserDTO;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface UserService {
    Map<String, Object> getUsersByUsernameContaining(String regex, int page);

    Map<String, Object> getUsers(int page);

    RequestUserDTO updateUser(RequestUserDTO requestUserDTO, Authentication authentication);

    RequestUserDTO blockUser(String username);

    RequestUserDTO unlockUser(String username);

    UserEntity findUserByAuth(Authentication authentication);

}
