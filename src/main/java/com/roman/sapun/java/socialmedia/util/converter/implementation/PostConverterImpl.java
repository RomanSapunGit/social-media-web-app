package com.roman.sapun.java.socialmedia.util.converter.implementation;

import com.roman.sapun.java.socialmedia.dto.post.ResponsePostDTO;
import com.roman.sapun.java.socialmedia.service.ImageService;
import com.roman.sapun.java.socialmedia.util.converter.PostConverter;
import com.roman.sapun.java.socialmedia.dto.post.RequestPostDTO;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.TagEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.util.IdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Set;

@Component
public class PostConverterImpl implements PostConverter {

    private final IdentifierGenerator identifierGenerator;
    private final ImageService imageService;

    @Autowired
    public PostConverterImpl(IdentifierGenerator identifierGenerator, ImageService imageService) {
        this.identifierGenerator = identifierGenerator;
        this.imageService = imageService;
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
                    imageService.getImagesByPost(post),
                    imageService.getImageByUser(post.getAuthor().getUsername()),
                    post.getUpvotes().size(),
                    post.getDownvotes().size());
    }
}
