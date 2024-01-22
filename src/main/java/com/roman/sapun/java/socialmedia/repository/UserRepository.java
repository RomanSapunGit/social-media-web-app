package com.roman.sapun.java.socialmedia.repository;

import com.roman.sapun.java.socialmedia.entity.ImageEntity;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
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
public interface UserRepository extends JpaRepository<UserEntity, Long> {
     @EntityGraph(attributePaths = {"posts", "userStatistics", "image"})
     List<UserEntity> findByPostsIn(List<PostEntity> posts);

     Optional<UserEntity> findByUsername(String username);
     Optional<UserEntity> findByToken(String token);
     Optional<UserEntity> findByEmail(String email);
     Optional<UserEntity> findByGoogleToken(String googleToken);
     Page<UserEntity> getAllByUsernameContaining(String username, Pageable pageable);
}