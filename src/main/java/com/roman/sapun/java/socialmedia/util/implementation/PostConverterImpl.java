package com.roman.sapun.java.socialmedia.util.implementation;

import com.roman.sapun.java.socialmedia.dto.RequestPostDTO;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.TagEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.util.PostConverter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Set;

@Component
public class PostConverterImpl implements PostConverter {
    @Override
    public PostEntity convertToPostEntity(RequestPostDTO postDTO, Set<TagEntity> tags, UserEntity user) {
        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(postDTO.title());
        postEntity.setDescription(postDTO.description());
        postEntity.setCreationTime(Timestamp.from(ZonedDateTime.now().toInstant()));
        postEntity.setTags(tags);
        postEntity.setAuthor(user);
        return postEntity;
    }
}
