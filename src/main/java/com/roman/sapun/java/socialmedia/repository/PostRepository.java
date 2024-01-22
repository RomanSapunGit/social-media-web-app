package com.roman.sapun.java.socialmedia.repository;

import com.roman.sapun.java.socialmedia.entity.CommentEntity;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.TagEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    Page<PostEntity> findPostEntitiesByTitleContaining(String title, Pageable pageable);

    Page<PostEntity> findPostEntitiesByAuthor(UserEntity user, Pageable pageable);

    Optional<PostEntity> findByIdentifier(String identifier);

    Page<PostEntity> getPostEntitiesByTagsContaining(TagEntity tag, Pageable pageable);

    @NonNull
    Page<PostEntity> findAll(@NonNull Pageable pageable);

    Page<PostEntity> findPostsByAuthorInAndCreationTimeBetween(Set<UserEntity> authors, Timestamp startTime, Timestamp endTime,
                                                               Pageable pageable);

    Optional<PostEntity> getPostEntityByIdentifier(String identifier);

    Optional<PostEntity> findPostEntityByCommentsContaining(CommentEntity comment);

    List<PostEntity> findAllByIdentifierIn(List<String> identifiers);
}
