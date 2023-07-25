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

    private String name;

    private String type;

    @Lob
    @Column(name = "image_data", length = 1000)
    private byte[] imageData;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity post;
    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
