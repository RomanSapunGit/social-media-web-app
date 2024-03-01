package com.roman.sapun.java.socialmedia.controller.graphql;

import com.roman.sapun.java.socialmedia.entity.CommentEntity;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.exception.CommentNotFoundException;
import com.roman.sapun.java.socialmedia.exception.PostNotFoundException;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import com.roman.sapun.java.socialmedia.service.SubscriptionService;
import com.roman.sapun.java.socialmedia.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Controller
public class UserQueryController {
    private final UserService userService;
    private final SubscriptionService subscriptionService;

    public UserQueryController(UserService userService, SubscriptionService subscriptionService) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
    }

    @QueryMapping()
    public boolean findFollowingByUsername(@Argument String username, Authentication authentication) throws UserNotFoundException {
        return subscriptionService.findFollowingByUsername(authentication, username);
    }

    @QueryMapping()
    public Page<UserEntity> getUsersByUsernameContaining(@Argument int page, @Argument int pageSize, @Argument String username) {
        return userService.getUsersByUsernameContaining(username, pageSize, page);
    }

    @QueryMapping()
    public Page<UserEntity> getUsers(@Argument int page, @Argument int pageSize) {
        return userService.getUsers(page, pageSize);
    }

    @BatchMapping(typeName = "Post")
    Mono<Map<PostEntity, UserEntity>> postAuthor(List<PostEntity> posts) throws UserNotFoundException, PostNotFoundException {
        return userService.getBatchedAuthorsForPosts(posts);
    }

    @BatchMapping(typeName = "Comment")
    Mono<Map<CommentEntity, UserEntity>> commentAuthor(List<CommentEntity> comments) throws CommentNotFoundException {
        return userService.getBatchedAuthorsForComments(comments);
    }

    /*@BatchMapping(typeName = "Post")
    Mono<Map<PostEntity, UserEntity>> author(List<PostEntity> posts) {
        System.out.println("get Author is being executed");

        List<UserEntity> usersWithPosts = userRepository.findByPostsIn(posts);

        Map<PostEntity, UserEntity> result = new HashMap<>();
        posts.forEach(post -> {
            UserEntity author = usersWithPosts.stream().parallel()
                    .filter(user -> user.getPosts().contains(post))
                    .findFirst()
                    .orElse(null);

            result.put(post, author);
        });

        return Mono.just(result);
    }
     */
}
