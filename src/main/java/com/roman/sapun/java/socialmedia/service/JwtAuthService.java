package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.credentials.TokenDTO;
import io.jsonwebtoken.Claims;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.function.Function;

public interface JwtAuthService {
    /**
     * <p>Generates Jwt token for User.</p>
     * @param username User's username.
     * @param password User's password.
     * @return DTO object containing username and token.
     */
    TokenDTO generateJwtToken(String username, String password);

    /**
     * <p>Generates token based on username.</p>
     * @return DTO object containing username and token.
     */
    TokenDTO generateJwtToken(String username);

    /**
     * <p>Extracts username from the given Jwt token.</p>
     * @param token jwt token we want to get username from.
     * @return usrname as string.
     */
    String extractUsername(String token);

    /**
     * <p>Extracts expiration date from the given Jwt token.</p>
     * @param token jwt token we want to get expiration date from.
     * @return expiration date of type Date.
     */
    Date extractExpiration(String token);

    /**
     * Extracts a specific claim from the given token using the provided claims function.
     *
     * @param token The token from which to extract the claim.
     * @param claimsFunction The function that takes the extracted claims and returns the desired value.
     * @param <T> The type of the desired value.
     * @return The value obtained by applying the claims function to the extracted claims.
     */
    <T> T extractClaim(String token, Function<Claims, T> claimsFunction);

    /**
     * <p>Validates whether the given Jwt token expired.</p>
     * @param token The token from which to check the expiration.
     * @return boolean is token expired.
     */
    Boolean isJwtTokenExpired(String token);

    /**
     * <p>Validates username extracted from token and the given username</p>
     * @return boolean result from validating
     */
    Boolean validateToken(String token, String usernameToMatch);
}
