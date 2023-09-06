package com.roman.sapun.java.socialmedia.repository;

import com.roman.sapun.java.socialmedia.entity.NotificationEntity;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByUserOrderByNotificationCreationDateDesc(UserEntity user);
    void delete(NotificationEntity notification);
}
