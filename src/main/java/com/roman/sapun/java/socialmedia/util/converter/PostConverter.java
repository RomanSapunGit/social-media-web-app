package com.roman.sapun.java.socialmedia.util.converter;

import com.roman.sapun.java.socialmedia.dto.post.RequestPostDTO;
import com.roman.sapun.java.socialmedia.dto.post.ResponsePostDTO;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.TagEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface PostConverter {
    PostEntity convertToPostEntity(RequestPostDTO postDTO, Set<TagEntity> tags, UserEntity user,
                                   PostEntity postEntity, Set<UserEntity> upvotes);

    ResponsePostDTO convertToResponsePostDTO(PostEntity post);

}
