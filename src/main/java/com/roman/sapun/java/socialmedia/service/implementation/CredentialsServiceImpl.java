package com.roman.sapun.java.socialmedia.service.implementation;

import com.roman.sapun.java.socialmedia.dto.ResetPassDTO;
import com.roman.sapun.java.socialmedia.dto.SignUpDTO;
import com.roman.sapun.java.socialmedia.dto.UserDTO;
import com.roman.sapun.java.socialmedia.entity.RoleEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.exception.TokenExpiredException;
import com.roman.sapun.java.socialmedia.exception.ValuesAreNotEqualException;
import com.roman.sapun.java.socialmedia.mail.MailSender;
import com.roman.sapun.java.socialmedia.repository.RoleRepository;
import com.roman.sapun.java.socialmedia.repository.UserRepository;
import com.roman.sapun.java.socialmedia.service.CredentialsService;
import com.roman.sapun.java.socialmedia.util.URLBuilder;
import jakarta.mail.MessagingException;
import com.roman.sapun.java.socialmedia.util.UserConverter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
@Service
public class CredentialsServiceImpl implements CredentialsService {

    private static final int TOKEN_EXPIRATION_HOURS = 24;
    private final UserConverter userConverter;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final URLBuilder urlBuilder;
    private final MailSender mailSender;

    @Autowired
    public CredentialsServiceImpl(UserConverter userConverter, RoleRepository roleRepository, UserRepository userRepository,
                                  PasswordEncoder passwordEncoder, URLBuilder urlBuilder, MailSender mailSender) {
        this.userConverter = userConverter;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.urlBuilder = urlBuilder;
        this.mailSender = mailSender;
    }

    @Override
    public UserDTO addNewUser(SignUpDTO signUpDto) {
        UserEntity createdUser = userConverter.convertToUserEntity(signUpDto, new UserEntity());
        RoleEntity role = roleRepository.findByName("ROLE_USER");
        createdUser.setRoles(Collections.singleton(role));
        userRepository.save(createdUser);
        return new UserDTO(createdUser);
    }

    @Override
    public ResetPassDTO resetPassword(String token, ResetPassDTO resetPassDTO) throws TokenExpiredException, ValuesAreNotEqualException {
        if (!resetPassDTO.password().equals(resetPassDTO.matchPassword())) {
            throw new ValuesAreNotEqualException();
        }
        UserEntity user = userRepository.findByToken(token);
        LocalDateTime tokenCreationDate = user.getTokenCreationDate();
        if (isTokenExpired(tokenCreationDate)) {
            user.setToken(null);
            user.setTokenCreationDate(null);
            throw new TokenExpiredException();
        }
        user.setPassword(passwordEncoder.encode(resetPassDTO.password()));
        user.setToken(null);
        user.setTokenCreationDate(null);
        userRepository.save(user);
        return resetPassDTO;
    }

    @Override
    public void sendEmail(String email, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        String userToken = setTokensByEmail(email);
        UriComponents uri = urlBuilder.buildUrl(request, userToken);
        mailSender.sendEmail(email, uri);
    }

    private boolean isTokenExpired(LocalDateTime tokenCreationDate) {
        LocalDateTime expirationTime = tokenCreationDate.plus(TOKEN_EXPIRATION_HOURS, ChronoUnit.SECONDS);
        return LocalDateTime.now().isAfter(expirationTime);
    }

    private String setTokensByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email);
        user.setToken(generateToken());
        user.setTokenCreationDate(LocalDateTime.now());
        user = userRepository.save(user);
        return user.getToken();
    }

    private String generateToken() {
        StringBuilder token = new StringBuilder();
        return String.valueOf(token.append(UUID.randomUUID()));
    }
}
