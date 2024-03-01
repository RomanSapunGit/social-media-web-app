package com.roman.sapun.java.socialmedia.controller.graphql;

import com.roman.sapun.java.socialmedia.entity.*;
import com.roman.sapun.java.socialmedia.exception.*;
import com.roman.sapun.java.socialmedia.service.PostService;
import com.roman.sapun.java.socialmedia.service.VoteService;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class PostQueryController {
    private final PostService postService;
    private final VoteService voteService;

    public PostQueryController(PostService postService, VoteService voteService) {
        this.postService = postService;
        this.voteService = voteService;
    }

    @QueryMapping()
    public Page<PostEntity> getPosts(@Argument int page, @Argument int size, @Argument String sortBy, Authentication authentication) throws InvalidPageSizeException, UserNotFoundException, TagNotFoundException {
        return postService.getPosts(authentication, page, size, sortBy);
    }

    @QueryMapping()
    public boolean isUpvoteMade(@Argument String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException {
        return voteService.isUpvoteMade(identifier, authentication).valid();
    }

    @QueryMapping()
    public boolean isDownvoteMade(@Argument String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException {
        return voteService.isDownvoteMade(identifier, authentication).valid();
    }

    @QueryMapping()
    public boolean isPostExistInSavedList(@Argument String identifier, Authentication authentication) throws UserNotFoundException, PostNotFoundException {
        return postService.isPostExistInSavedList(identifier, authentication);
    }

    @BatchMapping(typeName = "Post")
    Mono<Map<PostEntity, Set<UserEntity>>> upvotes(List<PostEntity> posts) throws UpvotesNotFoundException {
        return postService.getBatchedUpvotes(posts);
    }

    @BatchMapping(typeName = "Post")
    Mono<Map<PostEntity, Set<UserEntity>>> downvotes(List<PostEntity> posts) throws DownvotesNotFoundException {
        return postService.getBatchedDownvotes(posts);
    }

    @QueryMapping()
    public PostEntity getPostById(@Argument String id) {
        return postService.getPostById(id);
    }
}
