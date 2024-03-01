package com.roman.sapun.java.socialmedia.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.roman.sapun.java.socialmedia.dto.VoteDTO;
import com.roman.sapun.java.socialmedia.dto.credentials.ValidatorDTO;
import com.roman.sapun.java.socialmedia.exception.PostNotFoundException;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import com.roman.sapun.java.socialmedia.repository.PostRepository;
import com.roman.sapun.java.socialmedia.service.UserService;
import com.roman.sapun.java.socialmedia.service.VoteService;
import com.roman.sapun.java.socialmedia.util.TextExtractor;
import com.roman.sapun.java.socialmedia.util.converter.VoteConverter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoteServiceImpl implements VoteService {
    private final TextExtractor textExtractor;
    private final UserService userService;
    private final PostRepository postRepository;
    private final VoteConverter voteConverter;


    public VoteServiceImpl(TextExtractor textExtractor, UserService userService, PostRepository postRepository, VoteConverter voteConverter) {
        this.textExtractor = textExtractor;
        this.userService = userService;
        this.postRepository = postRepository;
        this.voteConverter = voteConverter;
    }

    @Override
    public List<VoteDTO> addUpvote(String identifier, Authentication authentication) throws JsonProcessingException, PostNotFoundException, UserNotFoundException {
        var identifierAsValue = textExtractor.extractIdentifierFromJson(identifier);
        var user = userService.findUserByAuth(authentication);
        var post = postRepository.findByIdentifier(identifierAsValue).orElseThrow(PostNotFoundException::new);
        post.getUpvotes().add(user);
        postRepository.save(post);
        return voteConverter.convertToVoteDTO(post.getUpvotes());
    }




    @Override
    public List<VoteDTO> removeUpvote(String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException {
        var user = userService.findUserByAuth(authentication);
        var post = postRepository.findByIdentifier(identifier).orElseThrow(PostNotFoundException::new);
        post.getUpvotes().remove(user);
        postRepository.save(post);
        return voteConverter.convertToVoteDTO(post.getUpvotes());
    }

    @Override
    public List<VoteDTO> addDownvote(String identifier, Authentication authentication) throws JsonProcessingException, UserNotFoundException, PostNotFoundException {
        var identifierAsValue = textExtractor.extractIdentifierFromJson(identifier);
        var user = userService.findUserByAuth(authentication);
        var post = postRepository.findByIdentifier(identifierAsValue).orElseThrow(PostNotFoundException::new);
        post.getDownvotes().add(user);
        postRepository.save(post);
        return voteConverter.convertToVoteDTO(post.getDownvotes());
    }

    @Override
    public List<VoteDTO> removeDownvote(String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException {
        var user = userService.findUserByAuth(authentication);
        var post = postRepository.findByIdentifier(identifier).orElseThrow(PostNotFoundException::new);
        post.getDownvotes().remove(user);
        postRepository.save(post);
        return voteConverter.convertToVoteDTO(post.getDownvotes());
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
