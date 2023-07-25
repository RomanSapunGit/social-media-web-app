package com.roman.sapun.java.socialmedia.dto.credentials;

import lombok.NonNull;


public record SignUpDTO(@NonNull String name,@NonNull String username, @NonNull String email, @NonNull String password ) {

}