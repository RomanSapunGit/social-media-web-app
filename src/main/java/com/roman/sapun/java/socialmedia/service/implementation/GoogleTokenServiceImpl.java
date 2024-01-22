package com.roman.sapun.java.socialmedia.service.implementation;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.roman.sapun.java.socialmedia.config.ValueConfig;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import com.roman.sapun.java.socialmedia.repository.UserRepository;
import com.roman.sapun.java.socialmedia.service.GoogleTokenService;
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
    public UserEntity getUserByGoogleId(String subToken) throws UserNotFoundException {
        return userRepository.findByGoogleToken(subToken).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public String extractSubFromIdToken(String authToken) throws GeneralSecurityException, IOException {
        var token = authToken != null && authToken.startsWith("Bearer ") ? authToken.substring(7) : authToken;
        var httpTransport = new NetHttpTransport();
        var verifier = new GoogleIdTokenVerifier.Builder(httpTransport, gsonFactory)
                .setAudience(Collections.singletonList(valueConfig.getClientId()))
                .build();
        var idToken = verifier.verify(token);
        return idToken.getPayload().getSubject();
    }


    @Override
    public void synchronizeGoogleUserWithDatabase(UserEntity user, GoogleIdToken idToken) {
        user.setEmail(getEmailFromGoogleToken(idToken));
        user.setName(getUsernameFromGoogleToken(idToken));
        user.setUsername(getUsernameFromGoogleToken( idToken));
        userRepository.save(user);
    }

    @Override
    public GoogleIdToken extractIdToken(String authToken) throws GeneralSecurityException, IOException {
        var token = authToken != null && authToken.startsWith("Bearer ") ? authToken.substring(7) : authToken;
        var httpTransport = new NetHttpTransport();
        var verifier = new GoogleIdTokenVerifier.Builder(httpTransport, gsonFactory)
                .setAudience(Collections.singletonList(valueConfig.getClientId()))
                .build();
        return verifier.verify(token);
    }

    private String getEmailFromGoogleToken(GoogleIdToken idToken) {
        if (idToken != null) {
            var payload = idToken.getPayload();
            return (String) payload.get("email");
        }
        return null;
    }

    private String getUsernameFromGoogleToken(GoogleIdToken idToken) {
        if (idToken != null) {
            var payload = idToken.getPayload();
            return (String) payload.get("name");
        }
        return null;
    }
}
