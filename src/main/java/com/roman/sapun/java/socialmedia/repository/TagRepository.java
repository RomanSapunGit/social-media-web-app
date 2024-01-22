package com.roman.sapun.java.socialmedia.repository;

import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.TagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {
    @EntityGraph(attributePaths = {"posts"})
    List<TagEntity> findByPostsIn( List<PostEntity> posts);
    boolean existsByName(String name);
    Set<TagEntity> findByNameContaining(String name);
    Page<TagEntity> findByNameContaining(String name, Pageable pageable);
    Optional<TagEntity> findByName(String name);
}
