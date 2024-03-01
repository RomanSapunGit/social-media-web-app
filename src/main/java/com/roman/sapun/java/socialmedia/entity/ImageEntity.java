package com.roman.sapun.java.socialmedia.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "images")
public class ImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identifier", unique = true, nullable = false)
    private String identifier;

    private String name;

    private String type;

    @Lob
    @Column(name = "image_data", length = 1000, nullable = false)
    private byte[] imageData;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
