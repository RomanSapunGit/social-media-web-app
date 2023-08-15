package com.roman.sapun.java.socialmedia.service.implementation;

import com.roman.sapun.java.socialmedia.config.ValueConfig;
import com.roman.sapun.java.socialmedia.dto.credentials.ValidatorDTO;
import com.roman.sapun.java.socialmedia.dto.user.RequestUserDTO;
import com.roman.sapun.java.socialmedia.dto.user.ResponseUserDTO;
import com.roman.sapun.java.socialmedia.repository.UserRepository;
import com.roman.sapun.java.socialmedia.service.UserService;
import com.roman.sapun.java.socialmedia.util.converter.PageConverter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.roman.sapun.java.socialmedia.entity.UserEntity;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PageConverter pageConverter;
    private final ValueConfig valueConfig;

    public UserServiceImpl(UserRepository userRepository, PageConverter pageConverter, ValueConfig valueConfig) {
        this.userRepository = userRepository;
        this.pageConverter = pageConverter;
        this.valueConfig = valueConfig;
    }

    @Override
    public Map<String, Object> getUsersByUsernameContaining(String username, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, valueConfig.getPageSize(), Sort.by(Sort.Direction.ASC, "username"));
        var matchedUsers = userRepository.getAllByUsernameContaining(username, pageable);
        var postDtoPage = matchedUsers.map(RequestUserDTO::new);
        return pageConverter.convertPageToResponse(postDtoPage);
    }

    @Override
    public Map<String, Object> getUsers(int page) {
        var pageable = PageRequest.of(page, valueConfig.getPageSize() - 45);
        var posts = userRepository.findAll(pageable);
        var postDtoPage = posts.map(ResponseUserDTO::new);
        return pageConverter.convertPageToResponse(postDtoPage);
    }

    @Override
    public RequestUserDTO updateUser(RequestUserDTO requestUserDTO, Authentication authentication) {
        var currentUser = findUserByAuth(authentication);

        currentUser.setName(requestUserDTO.name() != null ? requestUserDTO.name() : currentUser.getName());

        currentUser.setUsername(requestUserDTO.username() != null ? requestUserDTO.username() : currentUser.getUsername());

        currentUser.setEmail(requestUserDTO.email() != null ? requestUserDTO.email() : currentUser.getEmail());

        var updatedUser = userRepository.save(currentUser);
        return new RequestUserDTO(updatedUser);
    }

    @Override
    public ResponseUserDTO blockUser(String username) {
        var user = userRepository.findByUsername(username);
        user.setNotBlocked("false");
        userRepository.save(user);
        return new ResponseUserDTO(user);
    }

    @Override
    public ResponseUserDTO unlockUser(String username) {
        var user = userRepository.findByUsername(username);
        user.setNotBlocked("true");
        userRepository.save(user);
        return new ResponseUserDTO(user);
    }

    @Override
    public ResponseUserDTO addFollowing(Authentication authentication, String username) {
        var user = findUserByAuth(authentication);
        var userToFollow = userRepository.findByUsername(username);
        user.getFollowing().add(userToFollow);
        userRepository.save(user);
        return new ResponseUserDTO(userToFollow);
    }

    @Override
    public ValidatorDTO hasSubscriptions(Authentication authentication) {
        var user = findUserByAuth(authentication);
        return new ValidatorDTO(user.getFollowing().size() > 0);
    }

    @Override
    public ResponseUserDTO removeFollowing(Authentication authentication, String username) {
        var user = findUserByAuth(authentication);
        var userToFollow = userRepository.findByUsername(username);
        user.getFollowing().remove(userToFollow);
        userRepository.save(user);
        return new ResponseUserDTO(userToFollow);
    }

    @Override
    public UserEntity findUserByAuth(Authentication authentication) {
        var principal = authentication.getPrincipal();
        return userRepository.findByUsername(principal.toString());
    }
}
