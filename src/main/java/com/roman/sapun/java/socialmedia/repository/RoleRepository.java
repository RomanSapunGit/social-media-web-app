package com.roman.sapun.java.socialmedia.repository;

import com.roman.sapun.java.socialmedia.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
     Optional<RoleEntity> findByName(String name);
}
