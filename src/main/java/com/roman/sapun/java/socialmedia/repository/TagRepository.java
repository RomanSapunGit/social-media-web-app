package com.roman.sapun.java.socialmedia.repository;

import com.roman.sapun.java.socialmedia.entity.TagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;


@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {
    boolean existsByName(String name);
    Set<TagEntity> findByNameContaining(String name);
    Page<TagEntity> findByNameContaining(String name, Pageable pageable);
    Optional<TagEntity> findByName(String name);
}
