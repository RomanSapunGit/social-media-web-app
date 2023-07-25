package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.FileDTO;
import com.roman.sapun.java.socialmedia.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

@RestController
@RequestMapping("/api/v1/image")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public List<FileDTO> uploadFilesForUser(@RequestPart("image") List<MultipartFile> files,
                                     Authentication authentication) {
        return imageService.uploadImagesForUser(files, authentication);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{id}")
    public List<FileDTO> uploadFilesForPost(@PathVariable("id") String postId, @RequestPart("image") List<MultipartFile> files){
        return imageService.uploadImagesForPost(files, postId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public List<FileDTO> getImagesByPostIdentifier(@PathVariable("id") String postId) {
        return imageService.getImagesByPostIdentifier(postId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    public FileDTO getImageByUser(Authentication authentication) {
        return imageService.getImageByUser(authentication);
    }
}
