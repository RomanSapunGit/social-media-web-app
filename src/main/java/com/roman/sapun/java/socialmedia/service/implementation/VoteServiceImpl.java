package com.roman.sapun.java.socialmedia.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.roman.sapun.java.socialmedia.dto.credentials.ValidatorDTO;
import com.roman.sapun.java.socialmedia.dto.user.ResponseUserDTO;
import com.roman.sapun.java.socialmedia.exception.PostNotFoundException;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import com.roman.sapun.java.socialmedia.repository.PostRepository;
import com.roman.sapun.java.socialmedia.service.ImageService;
import com.roman.sapun.java.socialmedia.service.UserService;
import com.roman.sapun.java.socialmedia.service.VoteService;
import com.roman.sapun.java.socialmedia.util.TextExtractor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VoteServiceImpl implements VoteService {
    private final TextExtractor textExtractor;
    private final UserService userService;
    private final PostRepository postRepository;
    private final ImageService imageService;


    public VoteServiceImpl(TextExtractor textExtractor, UserService userService, PostRepository postRepository,
                           ImageService imageService) {
        this.textExtractor = textExtractor;
        this.userService = userService;
        this.postRepository = postRepository;
        this.imageService = imageService;
    }
    @Override
    public Set<ResponseUserDTO> addUpvote(String identifier, Authentication authentication) throws JsonProcessingException, PostNotFoundException, UserNotFoundException {
        var identifierAsValue = textExtractor.extractIdentifierFromJson(identifier);
        var user = userService.findUserByAuth(authentication);
        var post = postRepository.findByIdentifier(identifierAsValue).orElseThrow(PostNotFoundException::new);
        post.getUpvotes().add(user);
        postRepository.save(post);
        return post.getUpvotes().stream()
                .map(userEntity -> new ResponseUserDTO(userEntity, imageService.getImageByUser(userEntity.getUsername())))
                .filter(responseUserDTO -> responseUserDTO.userImage() != null)
                .collect(Collectors.toSet());
    }



    @Override
    public Set<ResponseUserDTO> removeUpvote(String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException {
        var user = userService.findUserByAuth(authentication);
        var post = postRepository.findByIdentifier(identifier).orElseThrow(PostNotFoundException::new);
        post.getUpvotes().remove(user);
        postRepository.save(post);
        return post.getUpvotes().stream()
                .map(userEntity -> new ResponseUserDTO(userEntity, imageService.getImageByUser(userEntity.getUsername())))
                .filter(responseUserDTO -> responseUserDTO.userImage() != null)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<ResponseUserDTO> addDownvote(String identifier, Authentication authentication) throws JsonProcessingException, UserNotFoundException, PostNotFoundException {
        var identifierAsValue = textExtractor.extractIdentifierFromJson(identifier);
        var user = userService.findUserByAuth(authentication);
        var post = postRepository.findByIdentifier(identifierAsValue).orElseThrow(PostNotFoundException::new);
        post.getDownvotes().add(user);
        postRepository.save(post);
        return post.getDownvotes().stream()
                .map(userEntity -> new ResponseUserDTO(userEntity, imageService.getImageByUser(userEntity.getUsername())))
                .filter(responseUserDTO -> responseUserDTO.userImage() != null)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<ResponseUserDTO> removeDownvote(String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException {
        var user = userService.findUserByAuth(authentication);
        var post = postRepository.findByIdentifier(identifier).orElseThrow(PostNotFoundException::new);
        post.getDownvotes().remove(user);
        postRepository.save(post);
        return post.getDownvotes().stream()
                .map(userEntity -> new ResponseUserDTO(userEntity, imageService.getImageByUser(userEntity.getUsername())))
                .filter(responseUserDTO -> responseUserDTO.userImage() != null)
                .collect(Collectors.toSet());
    }

    @Override
    public ValidatorDTO isUpvoteMade(String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException {
        var user = userService.findUserByAuth(authentication);
        var post = postRepository.findByIdentifier(identifier).orElseThrow(PostNotFoundException::new);
        return new ValidatorDTO(post.getUpvotes().contains(user));
    }

    @Override
    public ValidatorDTO isDownvoteMade(String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException {
        var user = userService.findUserByAuth(authentication);
        var post = postRepository.findByIdentifier(identifier).orElseThrow(PostNotFoundException::new);
        return new ValidatorDTO(post.getDownvotes().contains(user));
    }
}
