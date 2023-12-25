package com.roman.sapun.java.socialmedia.service.implementation;

import com.roman.sapun.java.socialmedia.entity.CommentEntity;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.entity.UserStatisticsEntity;
import com.roman.sapun.java.socialmedia.exception.UserStatisticsNotFoundException;
import com.roman.sapun.java.socialmedia.repository.CommentRepository;
import com.roman.sapun.java.socialmedia.repository.PostRepository;
import com.roman.sapun.java.socialmedia.repository.UserRepository;
import com.roman.sapun.java.socialmedia.repository.UserStatisticsRepository;
import com.roman.sapun.java.socialmedia.service.UserStatisticsService;
import com.roman.sapun.java.socialmedia.util.MailSender;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
@EnableAsync
@EnableTransactionManagement
public class UserStatisticsServiceImpl implements UserStatisticsService {

    private final UserRepository userRepository;
    private final UserStatisticsRepository userStatisticsRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final String EMAIL_MESSAGE_GREETING = "Hello, ";
    private final String EMAIL_SUBJECT = "Weekly user statistics";
    private final String EMAIL_TEXT = "\nYour statistics: created Posts: ";
    private final String CREATED_COMMENTS =  "\ncreated Comments: ";
    private final String VIEWED_POSTS = "\nviewed Posts: ";
    private final int MAX_STATISTIC_LIST_SIZE = 25;
    private final MailSender mailSender;

    public UserStatisticsServiceImpl(UserRepository userRepository, UserStatisticsRepository userStatisticsRepository,
                                     PostRepository postRepository, CommentRepository commentRepository,
                                     MailSender mailSender) {
        this.userRepository = userRepository;
        this.userStatisticsRepository = userStatisticsRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.mailSender = mailSender;
    }

    @Override
    public UserStatisticsEntity createUserStatistics(UserEntity user) {
        var userStatistics = new UserStatisticsEntity();
        userStatistics.setOnlineTimesDuration(new ArrayList<>());
        userStatistics.setViewedPosts(new ArrayList<>());
        userStatistics.setUser(user);
        return userStatistics;
    }

    @Override
    public void saveOnlineTime(String username, long onlineTime) {
        var userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        var userStatistics = userEntity.getUserStatistics();

        userStatistics.getOnlineTimesDuration().add(onlineTime);
        userRepository.save(userEntity);
    }

    @Override
    public void saveCreatedPostsStatistic(String username, Set<String> createdPosts) {
        var userEntity = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        var userStatistics = userEntity.getUserStatistics();
        postRepository.findAllByIdentifierIn(createdPosts.stream().toList())
                .forEach(userStatistics.getCreatedPosts()::add);
        userRepository.save(userEntity);
    }

    @Override
    public void saveCreatedCommentsStatistic(String username, Set<String> createdComments) {
        var userEntity = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        var userStatistics = userEntity.getUserStatistics();
        commentRepository.findAllByIdentifierIn(createdComments.stream().toList())
                .forEach(userStatistics.getCreatedComments()::add);
        userRepository.save(userEntity);
    }

    @Override
    public void saveViewedPostsStatistic(String username, Set<String> viewedPosts) throws UserStatisticsNotFoundException {
        var userEntity = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        var userStatistics = getUserStatistic(userEntity);
        postRepository.findAllByIdentifierIn(viewedPosts.stream().toList())
                .forEach(userStatistics.getViewedPosts()::add);
        userRepository.save(userEntity);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addCreatedPostToStatistic(UserEntity user, PostEntity post, HttpServletRequest request) throws UserStatisticsNotFoundException {
        var session = request.getSession();
        Set<String> createdPostsId = session.getAttribute("createdPostsId") == null ?
                new HashSet<>() : (Set<String>) session.getAttribute("createdPostsId");
        if (!createdPostsId.add(post.getIdentifier()))
            return;
        createdPostsId.add(post.getIdentifier());
        if (createdPostsId.size() > MAX_STATISTIC_LIST_SIZE) {
            var userStatistics = getUserStatistic(user);
            postRepository.findAllByIdentifierIn(createdPostsId.stream().toList())
                    .forEach(userStatistics.getCreatedPosts()::add);
            userRepository.save(user);
            session.setAttribute("createdPostsId", new HashSet<>());
            return;
        }
        session.setAttribute("createdPostsId", createdPostsId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addCreatedCommentToStatistic(UserEntity user, CommentEntity comment, HttpServletRequest request) throws UserStatisticsNotFoundException {
        var session = request.getSession();
        Set<String> createdCommentsId = session.getAttribute("createdCommentsId") == null ?
                new HashSet<>() : (Set<String>) session.getAttribute("createdCommentsId");
        if (!createdCommentsId.add(comment.getIdentifier()))
            return;
        createdCommentsId.add(comment.getIdentifier());
        if (createdCommentsId.size() > MAX_STATISTIC_LIST_SIZE) {
            var userStatistics = getUserStatistic(user);
            commentRepository.findAllByIdentifierIn(createdCommentsId.stream().toList())
                    .forEach(foundComment -> userStatistics.getCreatedComments().add(foundComment));
            userRepository.save(user);
            session.setAttribute("createdCommentsId", new HashSet<>());
            return;
        }
        session.setAttribute("createdCommentsId", createdCommentsId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addViewedPostToStatistic(UserEntity user, PostEntity post, HttpServletRequest request) throws UserStatisticsNotFoundException {
        var session = request.getSession();
        Set<String> viewedPosts = session.getAttribute("viewedPostsId") == null ?
                new HashSet<>() : (Set<String>) session.getAttribute("viewedPostsId");
        if (!viewedPosts.add(post.getIdentifier()))
            return;
        viewedPosts.add(post.getIdentifier());
        if (viewedPosts.size() > MAX_STATISTIC_LIST_SIZE) {
            var userStatistics = getUserStatistic(user);
            postRepository.findAllByIdentifierIn(viewedPosts.stream().toList())
                    .forEach(userStatistics.getViewedPosts()::add);
            userStatisticsRepository.save(userStatistics);
            session.setAttribute("viewedPostsId", new HashSet<>());
            return;
        }
        session.setAttribute("viewedPostsId", viewedPosts);
    }

    private UserStatisticsEntity getUserStatistic(UserEntity user) throws UserStatisticsNotFoundException {
        var userStatistics = user.getUserStatistics();

        if (userStatistics == null) {
            throw new UserStatisticsNotFoundException("User statistics not found for user: " + user.getUsername());
        }
        return userStatistics;
    }

    @Scheduled(cron = "0 0/10 * * * *")
    @Async
    @Transactional
    public void clearUserStatistics() {
        var statisticsList = userStatisticsRepository.findAll();

        statisticsList.forEach(statistic -> {
            statistic.getOnlineTimesDuration().clear();
            statistic.getCreatedPosts().clear();
            statistic.getCreatedComments().clear();
            statistic.getViewedPosts().clear();
        });
        userStatisticsRepository.saveAll(statisticsList);
    }

    @Scheduled(cron = "0 0 0 * * SUN")
    @Async
    @Transactional
    public void sendUserStatisticsToEmail() {
        var statisticsList = userStatisticsRepository.findAll();
        statisticsList.forEach(statistic -> {
            try {
                mailSender.sendEmail(statistic.getUser().getEmail(),
                        EMAIL_MESSAGE_GREETING + statistic.getUser().getUsername() + EMAIL_TEXT + statistic.getCreatedPosts().size()
                                + CREATED_COMMENTS + statistic.getCreatedComments().size() + VIEWED_POSTS +
                                statistic.getViewedPosts().size(), EMAIL_SUBJECT);
            } catch (MessagingException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
