package com.roman.sapun.java.socialmedia.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.roman.sapun.java.socialmedia.dto.credentials.ValidatorDTO;
import com.roman.sapun.java.socialmedia.dto.page.UserPageDTO;
import com.roman.sapun.java.socialmedia.dto.user.ConsentDTO;
import com.roman.sapun.java.socialmedia.dto.user.RequestUserDTO;
import com.roman.sapun.java.socialmedia.dto.user.ResponseUserDTO;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import com.roman.sapun.java.socialmedia.repository.UserRepository;
import com.roman.sapun.java.socialmedia.service.SubscriptionService;
import com.roman.sapun.java.socialmedia.service.UserService;
import com.roman.sapun.java.socialmedia.util.TextExtractor;
import com.roman.sapun.java.socialmedia.util.converter.ImageConverter;
import com.roman.sapun.java.socialmedia.util.converter.PageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService, SubscriptionService {
    private final UserRepository userRepository;
    private final PageConverter pageConverter;
    private final TextExtractor textExtractor;
    private final ImageConverter imageConverter;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PageConverter pageConverter,
                           TextExtractor textExtractor, ImageConverter imageConverter) {
        this.userRepository = userRepository;
        this.pageConverter = pageConverter;
        this.textExtractor = textExtractor;
        this.imageConverter = imageConverter;
    }

    @Override
    public UserPageDTO getUsersByUsernameContaining(String username, int pageNumber, int pageSize, String sortByValue) throws UserNotFoundException {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, sortByValue));
        var userEntityPage = userRepository.getAllByUsernameContaining(username, pageable);
        return pageConverter.convertPageToUserPageDTO(userEntityPage);
    }

    @Override
    public UserPageDTO getUsers(int page, int pageSize) throws UserNotFoundException {
        var pageable = PageRequest.of(page, pageSize);
        var users = userRepository.findAll(pageable);
        return pageConverter.convertPageToUserPageDTO(users);
    }

    @Override
    public RequestUserDTO updateUser(RequestUserDTO requestUserDTO, Authentication authentication) throws UserNotFoundException {
        var currentUser = findUserByAuth(authentication);

        currentUser.setName(requestUserDTO.name() != null ? requestUserDTO.name() : currentUser.getName());

        currentUser.setUsername(requestUserDTO.username() != null ? requestUserDTO.username() : currentUser.getUsername());

        currentUser.setEmail(requestUserDTO.email() != null ? requestUserDTO.email() : currentUser.getEmail());

        var updatedUser = userRepository.save(currentUser);
        return new RequestUserDTO(updatedUser);
    }


    @Override
    public ResponseUserDTO blockUser(String username) throws UserNotFoundException {
        var user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        user.setNotBlocked("false");
        userRepository.save(user);
        return new ResponseUserDTO(user, imageConverter.convertImageToDTO(user.getImage()));
    }

    @Override
    public ResponseUserDTO unlockUser(String username) throws UserNotFoundException {
        var user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        user.setNotBlocked("true");
        userRepository.save(user);
        return new ResponseUserDTO(user, imageConverter.convertImageToDTO(user.getImage()));
    }

    @Override
    public ResponseUserDTO addFollowing(Authentication authentication, String username) throws JsonProcessingException, UserNotFoundException {
        var user = findUserByAuth(authentication);
        var usernameAsValue = textExtractor.extractUsernameFromJson(username);
        var userToFollow = userRepository.findByUsername(usernameAsValue).orElseThrow(UserNotFoundException::new);
        user.getFollowing().add(userToFollow);
        userRepository.save(user);
        return new ResponseUserDTO(userToFollow, imageConverter.convertImageToDTO(userToFollow.getImage()));
    }

    @Override
    public ConsentDTO sendUserConsent(Authentication authentication, ConsentDTO consent) throws Exception {
        if(consent.consent() == null) throw new Exception("Consent cannot be null");
        var user = findUserByAuth(authentication);
        user.getUserStatistics().setConsent(consent.consent());
        userRepository.save(user);
        return new ConsentDTO(user.getUserStatistics().getConsent());
    }

    @Override
    public ConsentDTO getConsent(Authentication authentication) throws UserNotFoundException {
        var user = findUserByAuth(authentication);
        return new ConsentDTO(user.getUserStatistics().getConsent());
    }

    @Override
    public ValidatorDTO findFollowingByUsername(Authentication authentication, String username) throws UserNotFoundException {
        var user = findUserByAuth(authentication);
        return new ValidatorDTO(user.getFollowing().contains(userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new)));
    }

    @Override
    public ValidatorDTO hasSubscriptions(Authentication authentication) throws UserNotFoundException {
        var user = findUserByAuth(authentication);
        return new ValidatorDTO(!user.getFollowing().isEmpty());
    }

    @Override
    public ResponseUserDTO removeFollowing(Authentication authentication, String username) throws UserNotFoundException {
        var user = findUserByAuth(authentication);
        var userToFollow = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        user.getFollowing().remove(userToFollow);
        userRepository.save(user);
        return new ResponseUserDTO(userToFollow, imageConverter.convertImageToDTO(userToFollow.getImage()));
    }

    @Override
    public ResponseUserDTO getCurrentUser(Authentication authentication) throws UserNotFoundException {
        var user = findUserByAuth(authentication);
        return new ResponseUserDTO(user, imageConverter.convertImageToDTO(user.getImage()));
    }

    @Override
    public UserEntity findUserByAuth(Authentication authentication) throws UserNotFoundException {
        if ( authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userRepository.findByUsername(userDetails.getUsername()).orElseThrow(UserNotFoundException::new);
        }
        throw new AuthenticationCredentialsNotFoundException("User not found");
    }
}
