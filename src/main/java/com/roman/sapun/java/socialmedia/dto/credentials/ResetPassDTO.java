package com.roman.sapun.java.socialmedia.dto.credentials;

import lombok.NonNull;

public record ResetPassDTO(@NonNull String password,@NonNull String matchPassword) {
}
