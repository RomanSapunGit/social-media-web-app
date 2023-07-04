package com.roman.sapun.java.socialmedia.util.implementation;

import com.roman.sapun.java.socialmedia.dto.RequestPostDTO;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.TagEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.util.IdentifierGenerator;
import com.roman.sapun.java.socialmedia.util.PostConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Set;

@Component
public class PostConverterImpl implements PostConverter {
    private final IdentifierGenerator identifierGenerator;
    @Autowired
    public PostConverterImpl(IdentifierGenerator identifierGenerator) {
        this.identifierGenerator = identifierGenerator;
    }
    @Override
    public PostEntity convertToPostEntity(RequestPostDTO postDTO, Set<TagEntity> tags, UserEntity user, PostEntity postEntity) {
        postEntity.setTitle(postDTO.title());
        postEntity.setDescription(postDTO.description());
        postEntity.setCreationTime(Timestamp.from(ZonedDateTime.now().toInstant()));
        postEntity.setIdentifier(identifierGenerator.generateUniqueIdentifier());
        postEntity.setTags(tags);
        postEntity.setAuthor(user);
        return postEntity;
    }
}
