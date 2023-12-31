package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.entity.CommentEntity;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.entity.UserStatisticsEntity;
import com.roman.sapun.java.socialmedia.exception.UserStatisticsNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Set;

public interface UserStatisticsService {
    UserStatisticsEntity createUserStatistics(UserEntity user);

    void saveOnlineTime(String username, long onlineTime);

    void saveCreatedPostsStatistic(String username, Set<String> createdPosts);

    void saveCreatedCommentsStatistic(String username, Set<String> createdComments);

    void saveViewedPostsStatistic(String username, Set<String> viewedPosts) throws UserStatisticsNotFoundException;

    void addCreatedPostToStatistic(UserEntity user, PostEntity post, HttpServletRequest request) throws UserStatisticsNotFoundException;


    void addCreatedCommentToStatistic(UserEntity user, CommentEntity comment, HttpServletRequest request) throws UserStatisticsNotFoundException;


    void addViewedPostToStatistic(UserEntity user, PostEntity post, HttpServletRequest request) throws UserStatisticsNotFoundException;
}
