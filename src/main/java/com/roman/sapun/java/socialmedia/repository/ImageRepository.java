package com.roman.sapun.java.socialmedia.repository;

import com.roman.sapun.java.socialmedia.entity.ImageEntity;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import jakarta.persistence.Entity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
    @EntityGraph(attributePaths = {"post"})

    List<ImageEntity> findByPostIn( List<PostEntity> posts);

    ImageEntity findByUser(UserEntity user);
}
