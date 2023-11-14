package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.exception.InvalidValueException;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleTokenService {
    /**
     * <p>Generates Jwt token based on Google token.</p>
     *
     * @param token another token.
     * @return DTO object containing username and token.
     */
    String generateJwtTokenByGoogleToken(String authToken, HttpServletRequest request, HttpServletResponse response) throws GeneralSecurityException, IOException, UserNotFoundException, InvalidValueException;
    /**
     * <p>Validates whether it is a google token or not</p>
     * @param authToken The token from which to validate it
     * @return boolean
     */
    boolean isGoogleJwtToken(String authToken) throws GeneralSecurityException, IOException;
    /**
     * <p>Validates token and then returns extracted from the given Google token username.</p>
     * @param authToken token for extracting username.
     * @return username string.
     */
    String validateAndGetUsername(String authToken) throws GeneralSecurityException, IOException, UserNotFoundException;
}
