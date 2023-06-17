package com.roman.sapun.java.socialmedia.dto;

import lombok.NonNull;

public record ResetPassDTO(@NonNull String password,@NonNull String matchPassword) {
}
