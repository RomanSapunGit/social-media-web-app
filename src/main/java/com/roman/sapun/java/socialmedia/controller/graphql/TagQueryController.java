package com.roman.sapun.java.socialmedia.controller.graphql;

import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.TagEntity;
import com.roman.sapun.java.socialmedia.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class TagQueryController {
    private final TagService tagService;

    public TagQueryController(TagService tagService) {
        this.tagService = tagService;
    }

    @QueryMapping
    public Page<TagEntity> getTags(@Argument int page, @Argument int pageSize) {
        return tagService.getTags(page, pageSize);
    }
    @QueryMapping
    public Page<TagEntity> getTagsByTitleContaining(@Argument int page, @Argument int pageSize, @Argument String title) {
        return tagService.getExistingTagsFromText(title, pageSize, page);
    }

    @BatchMapping(typeName = "Post")
    Mono<Map<PostEntity, Set<TagEntity>>> tags(List<PostEntity> posts) {
        return tagService.getBatchedTags(posts);
    }
}
