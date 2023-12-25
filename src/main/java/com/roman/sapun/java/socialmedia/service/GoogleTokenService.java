package com.roman.sapun.java.socialmedia.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.exception.InvalidValueException;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleTokenService {

    /**
     * <p>Validates whether it is a google token or not</p>
     * @param authToken The token from which to validate it
     * @return boolean
     */
    UserEntity getUserByGoogleId(String authToken) throws UserNotFoundException;

    String extractSubFromIdToken(String authToken) throws GeneralSecurityException, IOException;

    void synchronizeGoogleUserWithDatabase(UserEntity user, GoogleIdToken idToken);

    GoogleIdToken extractIdToken(String authToken) throws GeneralSecurityException, IOException;
}
