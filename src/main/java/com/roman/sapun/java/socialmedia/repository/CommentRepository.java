package com.roman.sapun.java.socialmedia.repository;

import com.roman.sapun.java.socialmedia.entity.CommentEntity;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
CommentEntity findByIdentifier(String identifier);
Page<CommentEntity> findCommentEntitiesByPost(PostEntity post, Pageable pageable);
}
