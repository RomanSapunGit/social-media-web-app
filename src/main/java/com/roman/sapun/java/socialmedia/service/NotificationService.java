package com.roman.sapun.java.socialmedia.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.roman.sapun.java.socialmedia.dto.notification.NotificationDTO;
import com.roman.sapun.java.socialmedia.entity.UserEntity;

import java.util.List;

public interface NotificationService {


    void createFollowNotification(String message, String username);

    void createCommentNotification(String commentIdentifier, String message);

    List<NotificationDTO> getNotifications(String username);


    void sendMessage(String messageText, String date, String causedBy) throws JsonProcessingException;
}
