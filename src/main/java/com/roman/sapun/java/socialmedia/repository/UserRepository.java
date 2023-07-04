package com.roman.sapun.java.socialmedia.repository;

import com.roman.sapun.java.socialmedia.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
     UserEntity findByUsername(String username);
     UserEntity findByToken(String token);
     UserEntity findByEmail(String email);
}