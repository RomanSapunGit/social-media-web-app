package com.roman.sapun.java.socialmedia.repository;

import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.TagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    Page<PostEntity> findPostEntitiesByTagsIn(Set<TagEntity> tags, Pageable pageable);
    Page<PostEntity> findPostEntitiesByTitleContaining(String title, Pageable pageable);
    PostEntity findByIdentifier(String identifier);
}
