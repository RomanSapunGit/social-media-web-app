package com.roman.sapun.java.socialmedia.util.converter;

import com.roman.sapun.java.socialmedia.dto.comment.RequestCommentDTO;
import com.roman.sapun.java.socialmedia.entity.CommentEntity;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;

public interface CommentConverter {


    CommentEntity convertToCommentEntity(RequestCommentDTO requestCommentDTO, CommentEntity commentEntity, UserEntity user,
                                         PostEntity postEntity);
}
