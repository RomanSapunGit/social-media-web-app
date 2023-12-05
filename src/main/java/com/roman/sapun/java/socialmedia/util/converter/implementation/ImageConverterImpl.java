package com.roman.sapun.java.socialmedia.util.converter.implementation;

import com.roman.sapun.java.socialmedia.dto.image.RequestImageDTO;
import com.roman.sapun.java.socialmedia.entity.ImageEntity;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.util.IdentifierGenerator;
import com.roman.sapun.java.socialmedia.util.ImageUtil;
import com.roman.sapun.java.socialmedia.util.converter.ImageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ImageConverterImpl implements ImageConverter {

    private final ImageUtil imageUtil;
    private final IdentifierGenerator identifierGenerator;

    public ImageConverterImpl(ImageUtil imageUtil, IdentifierGenerator identifierGenerator) {
        this.imageUtil = imageUtil;
        this.identifierGenerator = identifierGenerator;
    }

    @Override
    public List<ImageEntity> convertImagesToEntity(List<RequestImageDTO> newImages, PostEntity post) {
        return newImages.stream().map(image -> convertImageToEntity(image, post)).collect(Collectors.toCollection(ArrayList::new));
    }

    private ImageEntity convertImageToEntity(RequestImageDTO imageDTO, PostEntity post) {
        var imageEntity = new ImageEntity();
        try {
            imageEntity.setImageData(imageUtil.compressImage(imageDTO.image().fileData()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        imageEntity.setIdentifier(identifierGenerator.generateUniqueIdentifier());
        imageEntity.setPost(post);
        imageEntity.setType(imageDTO.image().fileType());
        imageEntity.setName(imageDTO.image().fileName());
        return imageEntity;
    }
}
