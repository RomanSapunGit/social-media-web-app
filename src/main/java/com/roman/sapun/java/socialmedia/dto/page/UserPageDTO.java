package com.roman.sapun.java.socialmedia.dto.page;

import com.roman.sapun.java.socialmedia.dto.user.ResponseUserDTO;

import java.util.List;

public record UserPageDTO(
        List<ResponseUserDTO> entities,
        int currentPage,
        long total,
        int totalPages) {
}
