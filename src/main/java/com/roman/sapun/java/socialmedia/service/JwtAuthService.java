package com.roman.sapun.java.socialmedia.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.function.Function;

public interface JwtAuthService {
    String generateJwtToken(String username);

    String extractUsername(String token);

    Date extractExpiration(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsFunction);

    Boolean validateToken(String token, String usernameToMatch);
}
