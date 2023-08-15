package com.roman.sapun.java.socialmedia.dto.post;


import com.roman.sapun.java.socialmedia.dto.FileDTO;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record RequestPostDTO(String identifier, @NonNull String title, @NonNull String description) {

}
