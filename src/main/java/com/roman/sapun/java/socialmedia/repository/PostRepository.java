package com.roman.sapun.java.socialmedia.repository;

import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findPostEntitiesByTagsIn(Set<TagEntity> tags);
    List<PostEntity> findPostEntitiesByTitleContaining(String title);
}
