package com.roman.sapun.java.socialmedia.repository;

import com.roman.sapun.java.socialmedia.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
}
