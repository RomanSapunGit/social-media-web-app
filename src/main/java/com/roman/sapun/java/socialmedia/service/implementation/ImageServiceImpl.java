package com.roman.sapun.java.socialmedia.service.implementation;

import com.roman.sapun.java.socialmedia.dto.FileDTO;
import com.roman.sapun.java.socialmedia.entity.ImageEntity;
import com.roman.sapun.java.socialmedia.repository.ImageRepository;
import com.roman.sapun.java.socialmedia.repository.PostRepository;
import com.roman.sapun.java.socialmedia.service.ImageService;
import com.roman.sapun.java.socialmedia.service.UserService;
import com.roman.sapun.java.socialmedia.util.ImageUtil;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    private final ImageUtil imageUtil;

    public ImageServiceImpl(ImageRepository imageRepository, PostRepository postRepository, UserService userService,
                            ImageUtil imageUtil) {
        this.imageRepository = imageRepository;
        this.postRepository = postRepository;
        this.userService = userService;
        this.imageUtil = imageUtil;
    }

    @Override
    public List<FileDTO> uploadImagesForUser(List<MultipartFile> files, Authentication authentication) {
        return files.stream().map(file -> uploadImage(file, null, authentication)).collect(Collectors.toList());
    }

    @Override
    public List<FileDTO> uploadImagesForPost(List<MultipartFile> files, String postId) {
        return files.stream().map(file -> uploadImage(file, postId, null)).collect(Collectors.toList());
    }

    @Override
    public FileDTO uploadImage(MultipartFile file, String postId, Authentication authentication) {
        try {
            ImageEntity imageEntity = new ImageEntity();
            var post = postId != null ? postRepository.findByIdentifier(postId) : null;
            imageEntity.setPost(post);

            var user = authentication != null ? userService.findUserByAuth(authentication) : null;
            imageEntity.setUser(user);

            byte[] imageData = imageUtil.compressImage(file.getBytes());
            imageEntity.setImageData(imageData);
            imageEntity.setName(file.getOriginalFilename());
            imageEntity.setType(file.getContentType());

            ImageEntity savedImage = imageRepository.save(imageEntity);
            return new FileDTO(savedImage);
        } catch (IOException e) {
            throw new RuntimeException("Error processing file upload.", e);
        }
    }


    @Override
    public List<FileDTO> getImagesByPostIdentifier(String postId) {
        var post = postRepository.findByIdentifier(postId);
        var image = imageRepository.findByPost(post);
        return image.stream()
                .map(imageEntity -> new FileDTO
                        (imageEntity, imageUtil.decompressImage(imageEntity.getImageData())))
                .toList();
    }

    @Override
    public FileDTO getImageByUser(Authentication authentication) {
        var user = userService.findUserByAuth(authentication);
        var image = imageRepository.findByUser(user);
        return new FileDTO(image, imageUtil.decompressImage(image.getImageData()));
    }
}
