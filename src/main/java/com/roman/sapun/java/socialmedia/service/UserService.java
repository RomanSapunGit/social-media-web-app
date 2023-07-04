package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.UserDTO;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {
    List<UserDTO> getUsersByUsername(String regex, int page);

    UserDTO updateUser(UserDTO userDTO, Authentication authentication);

    UserEntity findUserByAuth(Authentication authentication);

}
