package com.roman.sapun.java.socialmedia.util.implementation;

import com.roman.sapun.java.socialmedia.dto.RequestCommentDTO;
import com.roman.sapun.java.socialmedia.entity.CommentEntity;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.util.CommentConverter;
import com.roman.sapun.java.socialmedia.util.IdentifierGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentConverterImpl implements CommentConverter {
    private final IdentifierGenerator identifierGenerator;
    @Autowired
    public CommentConverterImpl(IdentifierGenerator identifierGenerator) {
        this.identifierGenerator = identifierGenerator;
    }
    @Override
    public CommentEntity convertToCommentEntity(RequestCommentDTO requestCommentDTO, CommentEntity commentEntity, UserEntity user,
                                                PostEntity postEntity) {
        commentEntity.setPost(postEntity);
        commentEntity.setTitle(requestCommentDTO.title());
        commentEntity.setDescription(requestCommentDTO.description());
        commentEntity.setIdentifier(identifierGenerator.generateUniqueIdentifier());
        commentEntity.setAuthor(user);
        return commentEntity;
    }
}
