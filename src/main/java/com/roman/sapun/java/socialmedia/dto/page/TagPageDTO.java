package com.roman.sapun.java.socialmedia.dto.page;

import com.roman.sapun.java.socialmedia.dto.tag.TagDTO;

import java.util.List;

public record TagPageDTO(
        List<TagDTO> entities,
        int currentPage,
        long total,
        int totalPages) {
}
