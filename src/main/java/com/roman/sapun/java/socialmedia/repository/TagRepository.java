package com.roman.sapun.java.socialmedia.repository;

import com.roman.sapun.java.socialmedia.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {
    boolean existsByName(String name);
    TagEntity findByName(String name);
}
