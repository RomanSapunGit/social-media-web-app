package com.roman.sapun.java.socialmedia.service.implementation;

import com.roman.sapun.java.socialmedia.dto.credentials.*;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.dto.user.RequestUserDTO;
import com.roman.sapun.java.socialmedia.exception.*;
import com.roman.sapun.java.socialmedia.security.UserDetailsServiceImpl;
import com.roman.sapun.java.socialmedia.service.GoogleTokenService;
import com.roman.sapun.java.socialmedia.service.ImageService;
import com.roman.sapun.java.socialmedia.service.UserStatisticsService;
import com.roman.sapun.java.socialmedia.util.MailSender;
import com.roman.sapun.java.socialmedia.repository.RoleRepository;
import com.roman.sapun.java.socialmedia.repository.UserRepository;
import com.roman.sapun.java.socialmedia.service.AuthenticationService;
import com.roman.sapun.java.socialmedia.util.URLBuilder;
import jakarta.mail.MessagingException;
import com.roman.sapun.java.socialmedia.util.converter.UserConverter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final int TOKEN_EXPIRATION_HOURS = 24;
    private final UserConverter userConverter;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final URLBuilder urlBuilder;
    private final MailSender mailSender;
    private final AuthenticationManager authenticationManager;
    private final ImageService imageService;
    private final UserDetailsServiceImpl userDetailsService;
    private final SecurityContextRepository securityContextRepository;
    private final GoogleTokenService tokenService;
    private final UserStatisticsService userStatisticsService;


    @Autowired
    public AuthenticationServiceImpl(UserConverter userConverter, RoleRepository roleRepository, UserRepository userRepository,
                                     PasswordEncoder passwordEncoder, URLBuilder urlBuilder,
                                     MailSender mailSender, AuthenticationManager authenticationManager,
                                     ImageService imageService, UserDetailsServiceImpl userDetailsService,
                                     SecurityContextRepository securityContextRepository, GoogleTokenService tokenService,
                                     UserStatisticsService userStatisticsService) {
        this.userConverter = userConverter;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.urlBuilder = urlBuilder;
        this.mailSender = mailSender;
        this.authenticationManager = authenticationManager;
        this.imageService = imageService;
        this.userDetailsService = userDetailsService;
        this.securityContextRepository = securityContextRepository;
        this.tokenService = tokenService;
        this.userStatisticsService = userStatisticsService;
    }

    @Override
    public RequestUserDTO register(SignUpDTO signUpDto, MultipartFile image, HttpServletRequest request, HttpServletResponse response) throws IOException, InvalidValueException, UserNotFoundException, GeneralSecurityException {
        var createdUser = userConverter.convertToUserEntity(signUpDto, new UserEntity());
        if (signUpDto.googleIdentifier() != null) {
            var subToken = tokenService.extractSubFromIdToken(signUpDto.googleIdentifier());
            createdUser.setGoogleToken(subToken);
        }
        var role = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new InvalidValueException("Role not found"));
        createdUser.setRoles(Collections.singleton(role));
        var userStatistics = userStatisticsService.createUserStatistics(createdUser);
        createdUser.setUserStatistics(userStatistics);
        userRepository.save(createdUser);
        imageService.uploadImageForUser(image, signUpDto.username());
        authenticateUser(signUpDto.username(), request, response);
        return new RequestUserDTO(createdUser);
    }

    @Override
    public AuthRequestDTO loginUser(AuthRequestDTO authRequestDTO, HttpServletRequest request, HttpServletResponse response) throws InvalidValueException {
        if (!checkUserByCredentials(authRequestDTO.username(), authRequestDTO.password())) {
            throw new InvalidValueException("Username or password is incorrect");
        }
        authenticateUser(authRequestDTO.username(), request, response);
        return authRequestDTO;
    }

    @Override
    public TokenDTO loginUser(String token, HttpServletRequest request, HttpServletResponse response) throws UserNotFoundException,
            GeneralSecurityException, IOException {
        var subToken = tokenService.extractSubFromIdToken(token);
        var googleIdToken = tokenService.extractIdToken(token);
        var user = tokenService.getUserByGoogleId(subToken);
        tokenService.synchronizeGoogleUserWithDatabase(user, googleIdToken);
        authenticateUser(user.getUsername(), request, response);
        return new TokenDTO(token, user.getUsername());
    }

    @Override
    public boolean validateSession(HttpSession httpSession) {
        if (httpSession != null) {
            try {
                var username = httpSession.getAttribute("username");
                return username != null;
            } catch (IllegalStateException e) {
                return false;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void logout(HttpServletRequest request) throws UserStatisticsNotFoundException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            long loginTime = (long) session.getAttribute("loginTime");
            long onlineTime = System.currentTimeMillis() - loginTime;
            Set<String> createdPostsId = session.getAttribute("createdPostsId") == null ?
                    new HashSet<>() : (Set<String>) session.getAttribute("createdPostsId");
            Set<String> createdCommentsId = session.getAttribute("createdCommentsId") == null ?
                    new HashSet<>() : (Set<String>) session.getAttribute("createdCommentsId");
            Set<String> viewedPostsId = session.getAttribute("viewedPostsId") == null ?
                    new HashSet<>() : (Set<String>) session.getAttribute("viewedPostsId");
            userStatisticsService.saveOnlineTime((String) session.getAttribute("username"), onlineTime);
            userStatisticsService.saveCreatedPostsStatistic((String) session.getAttribute("username"), createdPostsId);
            userStatisticsService.saveCreatedCommentsStatistic((String) session.getAttribute("username"), createdCommentsId);
            userStatisticsService.saveViewedPostsStatistic((String) session.getAttribute("username"), viewedPostsId);
            session.invalidate();
        }
    }

    @Override
    public ResetPassDTO resetPassword(String token, ResetPassDTO resetPassDTO) throws TokenExpiredException, ValuesAreNotEqualException, UserNotFoundException {
        if (!resetPassDTO.password().equals(resetPassDTO.matchPassword())) {
            throw new ValuesAreNotEqualException();
        }
        var user = userRepository.findByToken(token).orElseThrow(UserNotFoundException::new);
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
    public void sendEmail(String email) throws MessagingException, UnsupportedEncodingException, UserNotFoundException {
        var userToken = setTokensByEmail(email);
        var uri = urlBuilder.buildUrl(userToken);
        mailSender.sendResetPassEmail(email, uri);
    }

    @Override
    public boolean checkUserByCredentials(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        var authentication = authenticationManager.authenticate
                (new UsernamePasswordAuthenticationToken(username, password));
        return authentication.isAuthenticated();
    }

    private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {
        var currentDateTime = LocalDateTime.now();
        var difference = Duration.between(tokenCreationDate, currentDateTime);
        return difference.toHours() >= TOKEN_EXPIRATION_HOURS;
    }

    private void authenticateUser(String username, HttpServletRequest request, HttpServletResponse response) {
        var userDetails = userDetailsService.loadUserByUsername(username);
        var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        var context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);
        securityContextRepository.saveContext(context, request, response);

        var session = request.getSession();
        session.setAttribute("username", username);
        session.setAttribute("loginTime", System.currentTimeMillis());
    }

    private String setTokensByEmail(String email) throws UserNotFoundException {
        var user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
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
