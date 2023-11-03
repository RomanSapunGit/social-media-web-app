package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.FileDTO;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.exception.PostNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {

    /**
     * <p>Saves image to database and bound it to User.</p>
     * @param image - image to save.
     * @param username - User's username to bound.
     * @return DTO object that contains image data.
     * @throws IOException if image compressing is failed.
     */
    FileDTO uploadImageForUser(MultipartFile image, String username) throws IOException;

    /**
     * <p>Saves images to database and bounds it to specific post.</p>
     * @param images to save.
     * @param postId post's generated unique identifier.
     * @param authentication for searching currently logged-in user.
     * @return list of images.
     */

    List<FileDTO> uploadImagesForPost(List<MultipartFile> images, String postId, Authentication authentication);

    /**
     * <p>Updates images for specific post.</p>
     *
     * @param images         to update.
     * @param postId         post's generated unique identifier.
     * @param authentication for searching currently logged-in user.
     * @return updated list of images.
     */

    List<FileDTO> updateImagesForPost(List<MultipartFile> images, String postId, Authentication authentication);

    /**
     * <p>Retrieve images by specific post.</p>
     * @param post we want to retrieve images from.
     * @return list of images.
     */
    List<FileDTO> getImagesByPost(PostEntity post);

    /**
     * <p>Get image by User.</p>
     * @param authentication for searching currently logged-in user..
     * @return image.
     */

    FileDTO getImageByUser(Authentication authentication);

    /**
     * <p>Get image by User's username.</p>
     * @param username User's username.
     * @return image.
     */
    FileDTO getImageByUser(String username);
}
