package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/v1/tag")
@RestController
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public Map<String, Object> getTags(@RequestParam int page) {
        return tagService.getTags(page);
    }
}
