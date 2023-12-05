package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.FileDTO;
import com.roman.sapun.java.socialmedia.dto.image.ResponseImageDTO;
import com.roman.sapun.java.socialmedia.exception.InvalidImageNumberException;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import com.roman.sapun.java.socialmedia.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/image")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Uploads an image for a user.
     *
     * @param file     The image file to upload.
     * @param username The username of the user.
     * @return The DTO representing the uploaded file.
     * @throws IOException If an error occurs while processing the image file.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public ResponseImageDTO uploadImageForUser(@RequestPart("image") MultipartFile file,
                                               @RequestPart("username") String username) throws IOException, UserNotFoundException {
        return imageService.uploadImageForUser(file, username);
    }

    /**
     * Uploads multiple images for a post.
     *
     * @param postId         The ID of the post.
     * @param files          The list of image files to upload.
     * @param authentication The authentication object representing the current user.
     * @return The list of DTOs representing the uploaded files.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{id}")
    public List<ResponseImageDTO> uploadImagesForPost(@PathVariable("id") String postId,
                                                      @RequestPart("image") List<MultipartFile> files, Authentication authentication) throws InvalidImageNumberException {
        return imageService.uploadImagesForPost(files, postId, authentication);
    }

    /**
     * Retrieves the image for the authenticated user.
     *
     * @param authentication The authentication object representing the current user.
     * @return The DTO representing the user's image.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping()
    public FileDTO getImageByUser(Authentication authentication) throws UserNotFoundException {
        return imageService.getImageByUser(authentication);
    }
}
