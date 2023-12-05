package com.roman.sapun.java.socialmedia.util.converter.implementation;

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

@Component
public class PageConverterImpl implements PageConverter {
    private final ImageService imageService;
    private final PostConverter postConverter;

    public PageConverterImpl(ImageService imageService, PostConverter postConverter) {
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
                new ResponseCommentDTO(comment, imageService.getImageByUser(comment.getAuthor().getUsername())));
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
        var postDTOForPage = page.map(postConverter::convertToResponsePostDTO);
        return new PostPageDTO(postDTOForPage.getContent(), postDTOForPage.getNumber(),
                postDTOForPage.getTotalElements(), postDTOForPage.getTotalPages());
    }
}
