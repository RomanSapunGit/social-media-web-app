package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.credentials.TokenDTO;
import io.jsonwebtoken.Claims;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.function.Function;

public interface JwtAuthService {
    TokenDTO generateJwtToken(String username, String password);

    TokenDTO generateJwtTokenByAnotherToken(String token) throws GeneralSecurityException, IOException;

    String validateAndGetUsername(String authToken) throws GeneralSecurityException, IOException;

    String validateTokenViaGoogleAndGetEmail(String authToken) throws GeneralSecurityException, IOException;

    TokenDTO generateJwtToken(String username);

    String extractUsername(String token);

    Date extractExpiration(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsFunction);

    Boolean isJwtTokenExpired(String token);

    Boolean validateToken(String token, String usernameToMatch);
}
