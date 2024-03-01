package com.roman.sapun.java.socialmedia.dto;

import com.roman.sapun.java.socialmedia.entity.ImageEntity;

public record FileDTO(String name, String type, byte[] imageData) {
    public FileDTO(ImageEntity image) {
        this(image.getName(), image.getType(), image.getImageData());
    }
    public FileDTO(ImageEntity image, byte[] imageData) {
        this(image.getName(), image.getType(), imageData);
    }
}
