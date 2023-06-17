package com.roman.sapun.java.socialmedia.dto;


import org.springframework.lang.NonNull;

public record RequestPostDTO(@NonNull String title, @NonNull String description) {

}
