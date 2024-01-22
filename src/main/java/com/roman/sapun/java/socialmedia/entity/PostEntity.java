package com.roman.sapun.java.socialmedia.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "creation_time")
    private Timestamp creationTime;

    @Column(unique = true, nullable = false)
    private String identifier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author", nullable = false)
    private UserEntity author;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "post_tags",
            joinColumns = {@JoinColumn(name = "post_id", nullable = false, referencedColumnName = "id")},
            inverseJoinColumns = @JoinColumn(name = "tag_id", nullable = false, referencedColumnName = "id"))
    private Set<TagEntity> tags;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post", cascade = CascadeType.ALL)
    private List<CommentEntity> comments;

    @OneToMany(fetch = FetchType.LAZY,  mappedBy = "post", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<ImageEntity> images;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "post_upvotes",
            joinColumns = {@JoinColumn(name = "post_id", nullable = false, referencedColumnName = "id")},
            inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id"))
    private Set<UserEntity> upvotes;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "post_downvotes",
            joinColumns = {@JoinColumn(name = "post_id", nullable = false, referencedColumnName = "id")},
            inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id"))
    private Set<UserEntity> downvotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statistics_id", nullable = false)
    private UserStatisticsEntity userStatistics;

    @ManyToMany(mappedBy = "savedPosts", fetch = FetchType.LAZY)
    private Set<UserEntity> users;
}
