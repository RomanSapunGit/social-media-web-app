package com.roman.sapun.java.socialmedia.repository;

import com.roman.sapun.java.socialmedia.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
     Optional<UserEntity> findByUsername(String username);
     Optional<UserEntity> findByToken(String token);
     Optional<UserEntity> findByEmail(String email);
     Optional<UserEntity> findByGoogleToken(String googleToken);
     Page<UserEntity> getAllByUsernameContaining(String username, Pageable pageable);
}