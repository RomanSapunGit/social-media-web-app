package com.roman.sapun.java.socialmedia.util.converter.implementation;

import com.roman.sapun.java.socialmedia.dto.FileDTO;
import com.roman.sapun.java.socialmedia.dto.post.ResponsePostDTO;
import com.roman.sapun.java.socialmedia.dto.user.ResponseUserDTO;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PostConverterImpl implements PostConverter {
    private final IdentifierGenerator identifierGenerator;
    private final ImageService imageService;
    private static final String POST_NOT_FOUND_EXCEPTION_RESPONSE = "Error converting post to response DTO: User not found.";
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
        try {
            Set<UserEntity> uniqueUsers = new HashSet<>();
            uniqueUsers.addAll(post.getUpvotes());
            uniqueUsers.addAll(post.getDownvotes());
            Map<String, FileDTO> userImages = imageService.getImagesByUsers(uniqueUsers);

            return new ResponsePostDTO(post,
                    imageService.getImagesByPost(post),
                    imageService.getImageByUser(post.getAuthor().getUsername()),
                    convertUsersToDTOs(post.getUpvotes(), userImages),
                    convertUsersToDTOs(post.getDownvotes(), userImages));
        } catch (UserNotFoundException e) {
            throw new RuntimeException(POST_NOT_FOUND_EXCEPTION_RESPONSE, e);
        }
    }

    private Set<ResponseUserDTO> convertUsersToDTOs(Set<UserEntity> users, Map<String, FileDTO> userImages) {
        return users.parallelStream()
                .map(user -> new ResponseUserDTO(user, userImages.get(user.getUsername())))
                .collect(Collectors.toSet());
    }
}
