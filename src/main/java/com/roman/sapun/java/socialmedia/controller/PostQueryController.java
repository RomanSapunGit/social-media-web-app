package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.entity.*;
import com.roman.sapun.java.socialmedia.exception.InvalidPageSizeException;
import com.roman.sapun.java.socialmedia.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class PostQueryController {
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final TagRepository tagRepository;
    private final RoleRepository roleRepository;
    private final CommentRepository commentRepository;
    public PostQueryController(PostRepository postRepository, RoleRepository roleRepository, ImageRepository imageRepository,
                               TagRepository tagRepository1, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.roleRepository = roleRepository;
        this.imageRepository = imageRepository;
        this.tagRepository = tagRepository1;
        this.commentRepository = commentRepository;
    }

    @QueryMapping()
    public Page<PostEntity> getPosts(@Argument int page, @Argument int size) throws InvalidPageSizeException {
        var pageable = PageRequest.of(page, size);
        System.out.println("get Posts is being executed");
        return postRepository.findAll(pageable);
    }

    @BatchMapping(typeName = "Post")
    Mono<Map<PostEntity, Set<TagEntity>>> tags(List<PostEntity> posts) {
        System.out.println("get Tags is being executed");

        List<TagEntity> allTags = tagRepository.findByPostsIn(posts);

        Map<PostEntity, Set<TagEntity>> result = new HashMap<>();

        posts.forEach(post -> {
            Set<TagEntity> postTags = allTags.stream()
                    .filter(tag -> tag.getPosts().contains(post))
                    .collect(Collectors.toSet());

            result.put(post, postTags);
        });

        return Mono.just(result);
    }

    @BatchMapping(typeName = "Post")
    Mono<Map<PostEntity, List<ImageEntity>>> images(List<PostEntity> posts) {
        System.out.println("get images is being executed");

        Map<Long, List<ImageEntity>> imagesByPostId = imageRepository.findByPostIn(posts)
                .stream()
                .collect(Collectors.groupingBy(image -> image.getPost().getId()));

        Map<PostEntity, List<ImageEntity>> result = posts.stream()
                .collect(Collectors.toMap(post -> post, post -> imagesByPostId.getOrDefault(post.getId(), Collections.emptyList())));

        return Mono.just(result);
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
    @BatchMapping(typeName = "Post")
    Mono<Map<PostEntity, UserEntity>> author( List<PostEntity> posts) {
        System.out.println("get Author is being executed");
        return Mono.just(posts.stream().collect(Collectors.toMap(post -> post, PostEntity::getAuthor)));
    }
      //TODO method for profile search (by user)

    @BatchMapping(typeName = "Post")
    Mono<Map<PostEntity, List<CommentEntity>>> comments( List<PostEntity> posts) {
        System.out.println("get Comments is being executed");

        Map<Long, List<CommentEntity>> commentsByPostId = commentRepository.findByPostIn(posts)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getPost().getId()));

        Map<PostEntity, List<CommentEntity>> result = posts.stream()
                .collect(Collectors.toMap(post -> post, post -> commentsByPostId.getOrDefault(post.getId(), Collections.emptyList())));

        return Mono.just(result);
    }

    @QueryMapping()
    public List<RoleEntity> getRoles() {
        System.out.println("get Roles is being executed");
        return roleRepository.findAll();
    }
    @QueryMapping()
    public PostEntity getPostById(@Argument Long id) {
        System.out.println("get Post by id is being executed");
        return postRepository.findById(id).orElse(null);
    }
}
