package com.roman.sapun.java.socialmedia.repository;

import com.roman.sapun.java.socialmedia.entity.UserStatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatisticsRepository extends JpaRepository<UserStatisticsEntity, Long> {
}
