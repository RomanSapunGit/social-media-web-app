package com.roman.sapun.java.socialmedia.dto.notification;

import com.roman.sapun.java.socialmedia.entity.NotificationEntity;

import java.sql.Timestamp;

public record NotificationDTO(String username, String message, String postIdentifier,String postTitle, Timestamp notificationCreationTime) {
    public NotificationDTO(NotificationEntity notification, String postIdentifier, String postTitle) {
        this(notification.getUser().getUsername(), notification.getMessage(),
                postIdentifier,postTitle, notification.getNotificationCreationDate());
    }
}
