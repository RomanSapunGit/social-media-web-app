package com.roman.sapun.java.socialmedia.service.implementation;

import com.roman.sapun.java.socialmedia.dto.UserDTO;
import com.roman.sapun.java.socialmedia.repository.UserRepository;
import com.roman.sapun.java.socialmedia.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.roman.sapun.java.socialmedia.entity.UserEntity;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static final int PAGE_SIZE = 50;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDTO> getUsersByUsername(String regex, int pageNumber) {
        var sanitizedRegex = Pattern.quote(regex);
        var allUsers = userRepository.findAll(); //TODO make an attempt for adding pagination in there
        var offset = pageNumber * PAGE_SIZE;
        return allUsers.stream()
                .filter(user -> user.getUsername().matches(sanitizedRegex + ".*"))
                .skip(offset)
                .map(UserDTO::new)
                .limit(PAGE_SIZE)
                .toList();
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
    public UserEntity findUserByAuth(Authentication authentication) {
        var principal = authentication.getPrincipal();
        return userRepository.findByUsername(principal.toString());
    }
}
