package com.roman.sapun.java.socialmedia.util.converter;

import com.roman.sapun.java.socialmedia.dto.FileDTO;
import com.roman.sapun.java.socialmedia.dto.credentials.SignUpDTO;
import com.roman.sapun.java.socialmedia.dto.user.ResponseUserDTO;
import com.roman.sapun.java.socialmedia.entity.UserEntity;

import java.util.Map;
import java.util.Set;

public interface UserConverter {
    UserEntity convertToUserEntity(SignUpDTO signUpDTO, UserEntity entity);
}
