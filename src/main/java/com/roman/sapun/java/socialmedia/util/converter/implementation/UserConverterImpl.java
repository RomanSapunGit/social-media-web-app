package com.roman.sapun.java.socialmedia.util.converter.implementation;

import com.roman.sapun.java.socialmedia.dto.FileDTO;
import com.roman.sapun.java.socialmedia.dto.user.UserDTO;
import com.roman.sapun.java.socialmedia.util.ImageUtil;
import com.roman.sapun.java.socialmedia.util.converter.UserConverter;
import com.roman.sapun.java.socialmedia.dto.credentials.SignUpDTO;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserConverterImpl implements UserConverter {
    private final PasswordEncoder passwordEncoder;
    private final ImageUtil imageUtil;
    @Autowired
    public UserConverterImpl(PasswordEncoder passwordEncoder, ImageUtil imageUtil) {
        this.passwordEncoder = passwordEncoder;
        this.imageUtil = imageUtil;
    }
    @Override
    public UserEntity convertToUserEntity(SignUpDTO signUpDTO, UserEntity entity) {
        entity.setName(signUpDTO.name());
        entity.setUsername(signUpDTO.username());
        entity.setEmail(signUpDTO.email());
        entity.setPassword(passwordEncoder.encode(signUpDTO.password()));
        return entity;
    }

    @Override
    public UserDTO convertToUserDTO(UserEntity user) {
        return new UserDTO(user, new FileDTO(user.getImage(),imageUtil.decompressImage(user.getImage().getImageData())));
    }
}
