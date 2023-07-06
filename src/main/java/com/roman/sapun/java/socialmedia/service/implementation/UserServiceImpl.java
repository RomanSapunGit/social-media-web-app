package com.roman.sapun.java.socialmedia.service.implementation;

import com.roman.sapun.java.socialmedia.config.ValueConfig;
import com.roman.sapun.java.socialmedia.dto.UserDTO;
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
    public Map<String, Object> getUsersByUsername(String username, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, valueConfig.getPageSize(), Sort.by(Sort.Direction.ASC, "username"));
        var matchedUsers = userRepository.getAllByUsernameContaining(username, pageable);
        var postDtoPage = matchedUsers.map(UserDTO::new);
        return pageConverter.convertPageToResponse(postDtoPage);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO, Authentication authentication) {
        var currentUser = findUserByAuth(authentication);

        currentUser.setName(userDTO.name() != null ? userDTO.name() : currentUser.getName());

        currentUser.setUsername(userDTO.username() != null ? userDTO.username() : currentUser.getUsername());

        currentUser.setEmail(userDTO.email() != null ? userDTO.email() : currentUser.getEmail());

        var updatedUser = userRepository.save(currentUser);
        return new UserDTO(updatedUser);
    }

    @Override
    public UserDTO blockUser(String username) {
        var user = userRepository.findByUsername(username);
        user.setNotBlocked(null);
        return new UserDTO(user);
    }

    @Override
    public UserDTO unlockUser(String username) {
        var user = userRepository.findByUsername(username);
        user.setNotBlocked("");
        return new UserDTO(user);
    }

    @Override
    public UserEntity findUserByAuth(Authentication authentication) {
        var principal = authentication.getPrincipal();
        return userRepository.findByUsername(principal.toString());
    }
}
