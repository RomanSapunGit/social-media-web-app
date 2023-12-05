package com.roman.sapun.java.socialmedia.dto.page;

import com.roman.sapun.java.socialmedia.dto.comment.ResponseCommentDTO;
import com.roman.sapun.java.socialmedia.dto.tag.TagDTO;

import java.util.List;

public record CommentPageDTO(
        List<ResponseCommentDTO> entities,
        int currentPage,
        long total,
        int totalPages) {
}
