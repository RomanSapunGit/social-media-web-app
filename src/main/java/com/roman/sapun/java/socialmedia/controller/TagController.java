package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.tag.TagDTO;
import com.roman.sapun.java.socialmedia.service.TagService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/v1/tag")
@RestController
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * Retrieves all tags.
     *
     * @param page The page number of the results.
     * @return A list containing 10 tags.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<TagDTO> getTags(@RequestParam int page) {
        return tagService.getTags(page);
    }
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{text}")
    @Cacheable(value = "tagCache", key = "#text.toString()", unless = "#result == null")
    public List<TagDTO> getTagsByText(@PathVariable String text) {
        return tagService.getTagsByText(text);
    }
}
