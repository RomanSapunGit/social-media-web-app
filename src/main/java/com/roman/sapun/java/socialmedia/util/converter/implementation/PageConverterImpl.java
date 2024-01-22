package com.roman.sapun.java.socialmedia.util.converter.implementation;

import com.roman.sapun.java.socialmedia.util.converter.ImageConverter;
import com.roman.sapun.java.socialmedia.util.converter.PageConverter;
import com.roman.sapun.java.socialmedia.util.converter.PostConverter;
import com.roman.sapun.java.socialmedia.dto.comment.ResponseCommentDTO;
import com.roman.sapun.java.socialmedia.dto.page.CommentPageDTO;
import com.roman.sapun.java.socialmedia.dto.page.PostPageDTO;
import com.roman.sapun.java.socialmedia.dto.page.TagPageDTO;
import com.roman.sapun.java.socialmedia.dto.page.UserPageDTO;
import com.roman.sapun.java.socialmedia.dto.tag.TagDTO;
import com.roman.sapun.java.socialmedia.dto.user.ResponseUserDTO;
import com.roman.sapun.java.socialmedia.entity.CommentEntity;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.TagEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import com.roman.sapun.java.socialmedia.service.ImageService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class PageConverterImpl implements PageConverter {
    private final ImageService imageService;
    private final ImageConverter imageConverter;
    private final PostConverter postConverter;

    public PageConverterImpl(ImageService imageService, PostConverter postConverter, ImageConverter imageConverter) {
        this.imageConverter = imageConverter;
        this.imageService = imageService;
        this.postConverter = postConverter;
    }

    @Override
    public TagPageDTO convertPageToTagPageDTO(Page<TagEntity> page) {
        var pageDTO = page.map(TagDTO::new);
        return new TagPageDTO(pageDTO.getContent(), pageDTO.getNumber(), pageDTO.getTotalElements(), pageDTO.getTotalPages());
    }

    @Override
    public CommentPageDTO convertPageToCommentPageDTO(Page<CommentEntity> commentPage) {
        var pageDTO = commentPage.map(comment ->
                new ResponseCommentDTO(comment, imageConverter.convertImageToDTO(comment.getAuthor().getImage())));
        return new CommentPageDTO(pageDTO.getContent(), pageDTO.getNumber(), pageDTO.getTotalElements(), pageDTO.getTotalPages());
    }

    @Override
    public UserPageDTO convertPageToUserPageDTO(Page<UserEntity> page) throws UserNotFoundException {
        var images = imageService.getImagesByUsers(new HashSet<>(page.getContent()));
        var pageDTO = page.map(user -> new ResponseUserDTO(user, images.get(user.getUsername())));
        return new UserPageDTO(pageDTO.getContent(), pageDTO.getNumber(), pageDTO.getTotalElements(), pageDTO.getTotalPages());
    }

    @Override
    public PostPageDTO convertPageToPostPageDTO(Page<PostEntity> page) {
        var postDTOForPage = page.stream().parallel().map(postConverter::convertToResponsePostDTO).collect(Collectors.toList());
        return new PostPageDTO(postDTOForPage, page.getNumber(),
                page.getTotalElements(), page.getTotalPages());
    }
}
