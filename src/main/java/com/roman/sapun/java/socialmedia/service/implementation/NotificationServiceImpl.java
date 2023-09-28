package com.roman.sapun.java.socialmedia.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roman.sapun.java.socialmedia.config.ValueConfig;
import com.roman.sapun.java.socialmedia.controller.SSEController;
import com.roman.sapun.java.socialmedia.dto.notification.NotificationDTO;
import com.roman.sapun.java.socialmedia.entity.NotificationEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.repository.CommentRepository;
import com.roman.sapun.java.socialmedia.repository.NotificationRepository;
import com.roman.sapun.java.socialmedia.repository.PostRepository;
import com.roman.sapun.java.socialmedia.repository.UserRepository;
import com.roman.sapun.java.socialmedia.service.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final SSEController sseController;
    private final UserRepository userRepository;
    private final ValueConfig valueConfig;
    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   PostRepository postRepository, CommentRepository commentRepository, SSEController sseController,
                                   UserRepository userRepository, ValueConfig valueConfig) {
        this.notificationRepository = notificationRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.sseController = sseController;
        this.userRepository = userRepository;
        this.valueConfig = valueConfig;
    }

    @Override
    public void createFollowNotification(String message, String username) {
        var user = userRepository.findByUsername(username);
        NotificationEntity notification = new NotificationEntity();
        notification.setMessage(message);
        notification.setUser(user);
        notification.setNotificationCreationDate(Timestamp.from(Instant.now()));
        notificationRepository.save(notification);
        sendNotificationToUser(user.getUsername(), message);
    }

    @Override
    public void createCommentNotification(String commentIdentifier, String message) {
        var comment = commentRepository.findByIdentifier(commentIdentifier);
        var post = postRepository.findPostEntityByCommentsContaining(comment);
        if (post.getAuthor().getUsername().equals(comment.getAuthor().getUsername())) {
            throw new IllegalArgumentException("Cannot notify on own post");
        }
        NotificationEntity notification = new NotificationEntity();
        notification.setMessage(message);
        notification.setPost(post);
        notification.setUser(post.getAuthor());
        notification.setNotificationCreationDate(Timestamp.from(Instant.now()));
        notificationRepository.save(notification);
        sendNotificationToUser(post.getAuthor().getUsername(), message);
    }

    @Override
    public List<NotificationDTO> getNotifications(String username) {
        var user = userRepository.findByUsername(username);
        deleteOldNotifications(user.getNotifications(), user);
        var notifications = notificationRepository.findByUserOrderByNotificationCreationDateDesc(user);

        return notifications.stream().map(notification ->
                new NotificationDTO(notification,
                        notification.getPost() != null ? notification.getPost().getIdentifier() : null,
                        notification.getPost() != null ? notification.getPost().getTitle() : null)).collect(Collectors.toList());
    }

    @Override
    public void sendMessage(String messageText, String date, String causedBy) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        var message = "\nmessage=" + messageText + "\ndate=" + date + "\ncausedBy=" + causedBy;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> body = Collections.singletonMap("text", message);
        String bodyAsString = objectMapper.writeValueAsString(body);
        HttpEntity<String> entity = new HttpEntity<>(bodyAsString, headers);
        restTemplate.exchange(valueConfig.getSlackWebhookUrl(), HttpMethod.POST, entity, String.class);
    }

    private void deleteOldNotifications(List<NotificationEntity> notificationEntities, UserEntity user) {
        var twelveHoursAgo = Timestamp.from(Instant.now().minusSeconds(valueConfig.getTwelveHours()));
        var notificationsToDelete = new ArrayList<NotificationEntity>();
        notificationEntities.stream()
                .filter(notificationEntity -> notificationEntity.getNotificationCreationDate().before(twelveHoursAgo))
                .forEach(notificationsToDelete::add);
        notificationEntities.removeAll(notificationsToDelete);
        user.setNotifications(notificationEntities);
    }


    public void sendNotificationToUser(String username, String notification) {
        sseController.sendNotification(username, notification);
    }
}
