package com.roman.sapun.java.socialmedia.util.converter;

import com.roman.sapun.java.socialmedia.dto.credentials.SignUpDTO;
import com.roman.sapun.java.socialmedia.entity.UserEntity;

public interface UserConverter {
    UserEntity convertToUserEntity(SignUpDTO signUpDTO, UserEntity entity);
}
