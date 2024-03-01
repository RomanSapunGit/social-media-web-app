package com.roman.sapun.java.socialmedia.controller.graphql;

import com.roman.sapun.java.socialmedia.entity.ImageEntity;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.service.ImageService;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Controller
public class ImageQueryController {
    private final ImageService imageService;

    public ImageQueryController(ImageService imageService) {
        this.imageService = imageService;
    }

    @BatchMapping(typeName = "Post")
    Mono<Map<PostEntity, List<ImageEntity>>> postImages(List<PostEntity> posts) {
        return imageService.getBatchedImages(posts);
    }
}
