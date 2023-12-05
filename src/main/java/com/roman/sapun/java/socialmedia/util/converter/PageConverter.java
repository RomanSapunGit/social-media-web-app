package com.roman.sapun.java.socialmedia.util.converter;

import com.roman.sapun.java.socialmedia.dto.page.CommentPageDTO;
import com.roman.sapun.java.socialmedia.dto.page.PostPageDTO;
import com.roman.sapun.java.socialmedia.dto.page.TagPageDTO;
import com.roman.sapun.java.socialmedia.dto.page.UserPageDTO;
import com.roman.sapun.java.socialmedia.entity.CommentEntity;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.TagEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import org.springframework.data.domain.Page;

public interface PageConverter {

    TagPageDTO convertPageToTagPageDTO(Page<TagEntity> page);

    CommentPageDTO convertPageToCommentPageDTO(Page<CommentEntity> commentPage);

    UserPageDTO convertPageToUserPageDTO(Page<UserEntity> page) throws UserNotFoundException;

    PostPageDTO convertPageToPostPageDTO(Page<PostEntity> page);
}
