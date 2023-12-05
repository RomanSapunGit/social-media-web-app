package com.roman.sapun.java.socialmedia.dto.image;

import com.roman.sapun.java.socialmedia.dto.FileDTO;
import com.roman.sapun.java.socialmedia.entity.ImageEntity;

public record ResponseImageDTO(String identifier, FileDTO fileDTO) {
}
