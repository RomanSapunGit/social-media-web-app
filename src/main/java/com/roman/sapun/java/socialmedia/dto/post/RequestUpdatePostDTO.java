package com.roman.sapun.java.socialmedia.dto.post;

import com.roman.sapun.java.socialmedia.dto.image.RequestImageDTO;
import org.springframework.lang.NonNull;

import java.util.List;

public record RequestUpdatePostDTO(String identifier, @NonNull String title, @NonNull String description,
                                   List<RequestImageDTO> images, List<RequestImageDTO> newImages) {
}
