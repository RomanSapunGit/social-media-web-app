package com.roman.sapun.java.socialmedia.service.implementation;

import com.roman.sapun.java.socialmedia.dto.credentials.ResetPassDTO;
import com.roman.sapun.java.socialmedia.dto.credentials.SignUpDTO;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.dto.user.RequestUserDTO;
import com.roman.sapun.java.socialmedia.exception.TokenExpiredException;
import com.roman.sapun.java.socialmedia.exception.ValuesAreNotEqualException;
import com.roman.sapun.java.socialmedia.util.mail.MailSender;
import com.roman.sapun.java.socialmedia.repository.RoleRepository;
import com.roman.sapun.java.socialmedia.repository.UserRepository;
import com.roman.sapun.java.socialmedia.service.CredentialsService;
import com.roman.sapun.java.socialmedia.util.URLBuilder;
import jakarta.mail.MessagingException;
import com.roman.sapun.java.socialmedia.util.converter.UserConverter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
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
    private final AuthenticationManager authenticationManager;


    @Autowired
    public CredentialsServiceImpl(UserConverter userConverter, RoleRepository roleRepository, UserRepository userRepository,
                                  PasswordEncoder passwordEncoder, URLBuilder urlBuilder,
                                  MailSender mailSender, AuthenticationManager authenticationManager) {
        this.userConverter = userConverter;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.urlBuilder = urlBuilder;
        this.mailSender = mailSender;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public RequestUserDTO addNewUser(SignUpDTO signUpDto) {
        var createdUser = userConverter.convertToUserEntity(signUpDto, new UserEntity());
        var role = roleRepository.findByName("ROLE_USER");
        createdUser.setRoles(Collections.singleton(role));
        userRepository.save(createdUser);
        return new RequestUserDTO(createdUser);
    }

    @Override
    public ResetPassDTO resetPassword(String token, ResetPassDTO resetPassDTO) throws TokenExpiredException, ValuesAreNotEqualException {
        if (!resetPassDTO.password().equals(resetPassDTO.matchPassword())) {
            throw new ValuesAreNotEqualException();
        }
        var user = userRepository.findByToken(token);
        var tokenCreationDate = user.getTokenCreationDate();
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
        var userToken = setTokensByEmail(email);
        var uri = urlBuilder.buildUrl(request, userToken);
        mailSender.sendEmail(email, uri);
    }

    @Override
    public boolean checkUserByCredentials(String username, String password) {
        var authentication = authenticationManager.authenticate
                (new UsernamePasswordAuthenticationToken(username, password));
        return authentication.isAuthenticated();
    }

    private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {
        var currentDateTime = LocalDateTime.now();
        var difference = Duration.between(tokenCreationDate, currentDateTime);
        return difference.toHours() >= TOKEN_EXPIRATION_HOURS;
    }

    private String setTokensByEmail(String email) {
        var user = userRepository.findByEmail(email);
        user.setToken(generateToken());
        user.setTokenCreationDate(LocalDateTime.now());
        user = userRepository.save(user);
        return user.getToken();
    }

    private String generateToken() {
        var token = new StringBuilder();
        return String.valueOf(token.append(UUID.randomUUID()));
    }

}
