package com.roman.sapun.java.socialmedia.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Table(name = "user_statistics")
@Entity
public class UserStatisticsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ElementCollection
    @CollectionTable(name = "online_times", joinColumns = @JoinColumn(name = "user_statistics_id"))
    @Column(name = "online_time_duration")
    private List<Long> onlineTimesDuration;

    @Column(name = "created_posts")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userStatistics", orphanRemoval = true)
    private List<PostEntity> createdPosts;

    @Column(name = "created_comments")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userStatistics", orphanRemoval = true)
    private List<CommentEntity> createdComments;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "viewed_posts",
            joinColumns = @JoinColumn(name = "user_statistics_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "post_id", nullable = false))
    private List<PostEntity> viewedPosts;
}
