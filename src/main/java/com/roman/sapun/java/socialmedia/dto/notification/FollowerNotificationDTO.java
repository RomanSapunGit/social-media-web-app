package com.roman.sapun.java.socialmedia.dto.notification;

import com.roman.sapun.java.socialmedia.entity.UserEntity;

import java.sql.Timestamp;

public record FollowerNotificationDTO(String username, String message,
                                      Timestamp notificationCreationTime) {
public FollowerNotificationDTO(UserEntity user, String message, Timestamp notificationCreationTime) {
    this(user.getUsername(), message, notificationCreationTime);
}
}
