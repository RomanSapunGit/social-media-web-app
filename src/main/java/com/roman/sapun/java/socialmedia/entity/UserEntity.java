package com.roman.sapun.java.socialmedia.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Table(name = "users")
@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(name = "not_blocked")
    private String notBlocked;
    private String token;
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime tokenCreationDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")},
            inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false, referencedColumnName = "id"))
    private Set<RoleEntity> roles;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_followers",
            joinColumns = @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "following_id", nullable = false, referencedColumnName = "id"))
    private Set<UserEntity> following = new HashSet<>();

    @ManyToMany(mappedBy = "following", fetch = FetchType.EAGER)
    private Set<UserEntity> followers = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    private List<PostEntity> posts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    private List<CommentEntity> comments;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private ImageEntity image;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<NotificationEntity> notifications;

    @PrePersist
    public void prePersist() {
        if (notBlocked == null) {
            notBlocked = "true";
        }
    }
}