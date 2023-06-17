package com.roman.sapun.java.socialmedia.dto;

import lombok.NonNull;


public record SignUpDTO(@NonNull String name,@NonNull String username, @NonNull String email, @NonNull String password ) {

}