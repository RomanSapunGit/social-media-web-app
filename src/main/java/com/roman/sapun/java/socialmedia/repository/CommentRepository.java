package com.roman.sapun.java.socialmedia.repository;

import com.roman.sapun.java.socialmedia.entity.CommentEntity;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @EntityGraph(attributePaths = {"post"})
    List<CommentEntity> findByPostIn( List<PostEntity> posts);

    Optional<CommentEntity> findByIdentifier(String identifier);

    @EntityGraph(attributePaths = {"post", "author", "author.image"})
    Page<CommentEntity> findCommentEntitiesByPostIdentifier(String identifier, Pageable pageable);

    List<CommentEntity> findAllByIdentifierIn(List<String> identifiers);
}
