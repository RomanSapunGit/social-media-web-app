package com.roman.sapun.java.socialmedia.controller.graphql;

import com.roman.sapun.java.socialmedia.entity.CommentEntity;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.exception.CommentNotFoundException;
import com.roman.sapun.java.socialmedia.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Controller
public class CommentQueryController {
    private final CommentService commentService;

    public CommentQueryController(CommentService commentService) {
        this.commentService = commentService;
    }

    @QueryMapping()
    public Page<CommentEntity> getComments(@Argument String postId, @Argument int pageNumber) throws CommentNotFoundException {
        return commentService.getCommentsByPostIdentifier(postId, pageNumber);
    }

    @BatchMapping(typeName = "Post")
    Mono<Map<PostEntity, List<CommentEntity>>> comments(List<PostEntity> posts) {
        return commentService.getBatchedComments(posts);
    }

}
