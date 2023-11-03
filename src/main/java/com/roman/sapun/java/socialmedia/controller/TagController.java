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
     * Retrieves a paginated list of tags.
     *
     * @param page     The page number of the results.
     * @param pageSize The number of tags to display per page (default is 5).
     * @return A list containing a paginated set of tags for the specified page.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    @Cacheable(value = "tagCache", key = "#page + '-' + #pageSize", unless = "#result == null")
    public List<TagDTO> getTags(@RequestParam int page, @RequestParam(defaultValue = "5") int pageSize) {
        return tagService.getTags(page, pageSize);
    }
    /**
     * Retrieves a list of tags that match the provided text.
     *
     * @param text The text used to search for matching tags.
     * @return A list of tags containing the provided text, or an empty list if no matches are found.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{text}")
    @Cacheable(value = "tagCache", key = "#text.toString()", unless = "#result == null")
    public List<TagDTO> getTagsByText(@PathVariable String text) {
        return tagService.getTagsByText(text);
    }
}
