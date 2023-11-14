package com.roman.sapun.java.socialmedia.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.roman.sapun.java.socialmedia.dto.notification.NotificationDTO;
import com.roman.sapun.java.socialmedia.exception.CommentNotFoundException;
import com.roman.sapun.java.socialmedia.exception.PostNotFoundException;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;

import java.util.List;

public interface NotificationService {


    void createFollowNotification(String message, String username) throws UserNotFoundException;

    void createCommentNotification(String commentIdentifier, String message) throws CommentNotFoundException, PostNotFoundException;

    List<NotificationDTO> getNotifications(String username) throws UserNotFoundException;


    void sendMessage(String messageText, String date, String causedBy) throws JsonProcessingException;
}
