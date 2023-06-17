package com.roman.sapun.java.socialmedia.repository;

import com.roman.sapun.java.socialmedia.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
     UserEntity findByUsername(String username);
     UserEntity findByToken(String token);
     UserEntity findByEmail(String email);
     List<UserEntity> getAllByUsername(String username);
}