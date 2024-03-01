package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.page.TagPageDTO;
import com.roman.sapun.java.socialmedia.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/v1/tag")
public class TagController {
    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{text}")
    public TagPageDTO getTagsByText(@PathVariable String text,
                                    @RequestParam(defaultValue = "5") int pageSize,
                                    @RequestParam int page) {
        return tagService.getExistingTagsByText(text, pageSize, page);
    }
}
