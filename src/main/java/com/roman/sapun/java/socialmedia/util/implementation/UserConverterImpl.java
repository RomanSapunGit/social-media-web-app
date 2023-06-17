package com.roman.sapun.java.socialmedia.util.implementation;

import com.roman.sapun.java.socialmedia.dto.SignUpDTO;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.util.UserConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserConverterImpl implements UserConverter {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserConverterImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public UserEntity convertToUserEntity(SignUpDTO signUpDTO, UserEntity entity) {
        entity.setName(signUpDTO.name());
        entity.setUsername(signUpDTO.username());
        entity.setEmail(signUpDTO.email());
        entity.setPassword(passwordEncoder.encode(signUpDTO.password()));
        return entity;
    }
}
