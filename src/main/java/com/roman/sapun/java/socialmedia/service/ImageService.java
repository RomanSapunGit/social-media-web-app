package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.FileDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

public interface ImageService {


    List<FileDTO> uploadImagesForUser(List<MultipartFile> files, Authentication authentication);

    List<FileDTO> uploadImagesForPost(List<MultipartFile> files, String postId);

    FileDTO uploadImage(MultipartFile file, String postId, Authentication authentication);

    List<FileDTO> getImagesByPostIdentifier(String postId);

    FileDTO getImageByUser(Authentication authentication);
}
