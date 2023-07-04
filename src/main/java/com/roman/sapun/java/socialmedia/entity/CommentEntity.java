package com.roman.sapun.java.socialmedia.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

    @Entity
    @Getter
    @Setter
    @Table(name = "comments")
    public class CommentEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(nullable = false)
        private String title;
        @Column(nullable = false)
        private String description;
        @Column(unique = true, nullable = false)
        private String identifier;

        @ManyToOne
        @JoinColumn(name = "user_id")
        private UserEntity author;

        @ManyToOne
        @JoinColumn(name = "post_id")
        private PostEntity post;
    }
