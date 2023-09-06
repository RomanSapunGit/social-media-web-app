package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.credentials.TokenDTO;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface ExternalJwtTokenAuthService {
    /**
     * <p>Generates Jwt token based on Google token.</p>
     * @param token another token.
     * @return DTO object containing username and token.
     */
    TokenDTO generateJwtTokenByGoogleToken(String token) throws GeneralSecurityException, IOException;
    /**
     * <p>Validates whether it is a google token or not</p>
     * @param authToken The token from which to validate it
     * @return boolean
     */
    boolean isJwtTokenAGoogle(String authToken) throws GeneralSecurityException, IOException;
    /**
     * <p>Validates token and then returns extracted from the given Google token username.</p>
     * @param authToken token for extracting username.
     * @return username string.
     */
    String validateAndGetUsername(String authToken) throws GeneralSecurityException, IOException;
}
