package com.roman.sapun.java.socialmedia.util.converter.implementation;

import com.roman.sapun.java.socialmedia.dto.post.ResponsePostDTO;
import com.roman.sapun.java.socialmedia.util.converter.ImageConverter;
import com.roman.sapun.java.socialmedia.util.converter.PostConverter;
import com.roman.sapun.java.socialmedia.dto.post.RequestPostDTO;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.TagEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.util.IdentifierGenerator;
import com.roman.sapun.java.socialmedia.util.converter.UserConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PostConverterImpl implements PostConverter {

    private final IdentifierGenerator identifierGenerator;
    private final ImageConverter imageConverter;
    private final UserConverter userConverter;

    @Autowired
    public PostConverterImpl(IdentifierGenerator identifierGenerator, ImageConverter imageConverter, UserConverter userConverter) {
        this.identifierGenerator = identifierGenerator;
        this.imageConverter = imageConverter;
        this.userConverter = userConverter;
    }

    @Override
    public PostEntity convertToPostEntity(RequestPostDTO postDTO, Set<TagEntity> tags, UserEntity user,
                                          PostEntity postEntity, Set<UserEntity> upvotes) {
        postEntity.setTitle(postDTO.title());
        postEntity.setDescription(postDTO.description());
        postEntity.setCreationTime(Timestamp.from(ZonedDateTime.now().toInstant()));
        postEntity.setIdentifier(identifierGenerator.generateUniqueIdentifier());
        postEntity.setUpvotes(upvotes);
        postEntity.setTags(tags);
        postEntity.setAuthor(user);
        return postEntity;
    }

    @Override
    public ResponsePostDTO convertToResponsePostDTO(PostEntity post) {
            return new ResponsePostDTO(post,
                    imageConverter.convertImagesToResponseImageDTO(post.getPostImages()),
                    imageConverter.convertImageToDTO(post.getAuthor().getImage()),
                    post.getUpvotes().stream().map(userConverter::convertToUserDTO).collect(Collectors.toList()),
                    post.getDownvotes().stream().map(userConverter::convertToUserDTO).collect(Collectors.toList()));
    }
}
