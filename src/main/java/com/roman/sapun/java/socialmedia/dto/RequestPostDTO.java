package com.roman.sapun.java.socialmedia.dto;


import org.springframework.lang.NonNull;

public record RequestPostDTO(String identifier, @NonNull String title, @NonNull String description, String username) {

}
