package com.roman.sapun.java.socialmedia.service.implementation;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.roman.sapun.java.socialmedia.config.ValueConfig;
import com.roman.sapun.java.socialmedia.exception.InvalidValueException;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import com.roman.sapun.java.socialmedia.repository.UserRepository;
import com.roman.sapun.java.socialmedia.service.GoogleTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleTokenServiceImpl implements GoogleTokenService {
    private final ValueConfig valueConfig;
    private final GsonFactory gsonFactory;
    private final UserRepository userRepository;


    @Autowired
    public GoogleTokenServiceImpl(ValueConfig valueConfig, GsonFactory gsonFactory,
                                  UserRepository userRepository) {
        this.gsonFactory = gsonFactory;
        this.valueConfig = valueConfig;
        this.userRepository = userRepository;
    }

    @Override
    public String validateAndGetUsername(String authToken) throws GeneralSecurityException, IOException, UserNotFoundException {
        if (!isGoogleJwtToken(authToken)) {
            return null;
        }
        var email = getEmailFromGoogleToken(authToken);
        var user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User with email " + email + " not found"));
        return user.getUsername();
    }

    @Override
    public String generateJwtTokenByGoogleToken(String authToken, HttpServletRequest request, HttpServletResponse response)
            throws GeneralSecurityException, IOException, UserNotFoundException, InvalidValueException {
        authToken = authToken != null && authToken.startsWith("Bearer ") ? authToken.substring(7) : authToken;
        if (!isGoogleJwtToken(authToken)) {
            throw new InvalidValueException("Invalid token");
        }
        return userRepository.findByEmail(getEmailFromGoogleToken(authToken)).orElseThrow(UserNotFoundException::new).getUsername();
    }

    @Override
    public boolean isGoogleJwtToken(String authToken) throws GeneralSecurityException, IOException {
        var idToken = extractIdToken(authToken);
        return idToken != null;
    }

    private String getEmailFromGoogleToken(String authToken) throws GeneralSecurityException, IOException {
        var idToken = extractIdToken(authToken);
        if (idToken != null) {
            var payload = idToken.getPayload();
            return (String) payload.get("email");
        }
        return null;
    }

    private GoogleIdToken extractIdToken(String authToken) throws GeneralSecurityException, IOException {
        var httpTransport = new com.google.api.client.http.javanet.NetHttpTransport();
        var verifier = new GoogleIdTokenVerifier.Builder(httpTransport, gsonFactory)
                .setAudience(Collections.singletonList(valueConfig.getClientId()))
                .build();
        return verifier.verify(authToken);
    }
}
