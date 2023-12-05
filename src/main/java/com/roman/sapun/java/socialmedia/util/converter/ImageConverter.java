package com.roman.sapun.java.socialmedia.util.converter;

import com.roman.sapun.java.socialmedia.dto.image.RequestImageDTO;
import com.roman.sapun.java.socialmedia.entity.ImageEntity;
import com.roman.sapun.java.socialmedia.entity.PostEntity;

import java.util.List;

public interface ImageConverter {
    List<ImageEntity> convertImagesToEntity(List<RequestImageDTO> newImages, PostEntity post);
}
