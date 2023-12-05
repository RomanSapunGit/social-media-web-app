package com.roman.sapun.java.socialmedia.dto.page;

import com.roman.sapun.java.socialmedia.dto.post.ResponsePostDTO;

import java.util.List;

public record PostPageDTO(
        List<ResponsePostDTO> entities,
        int currentPage,
        long total,
        int totalPages) {
}
