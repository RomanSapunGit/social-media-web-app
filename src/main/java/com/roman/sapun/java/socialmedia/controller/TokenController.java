package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.credentials.AuthRequestDTO;
import com.roman.sapun.java.socialmedia.dto.credentials.TokenDTO;
import com.roman.sapun.java.socialmedia.dto.credentials.ValidatorDTO;
import com.roman.sapun.java.socialmedia.service.ExternalJwtTokenAuthService;
import com.roman.sapun.java.socialmedia.service.JwtAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/v1/token")
public class TokenController {

    private final JwtAuthService jwtAuthService;
    private final ExternalJwtTokenAuthService externalJwtTokenAuthService;

    @Autowired
    public TokenController(JwtAuthService jwtAuthService, ExternalJwtTokenAuthService externalJwtTokenAuthService) {
        this.jwtAuthService = jwtAuthService;
        this.externalJwtTokenAuthService = externalJwtTokenAuthService;
    }

    /**
     * Generates a JWT token based on the provided authentication request.
     *
     * @param authRequestDTO The authentication request containing username and password.
     * @return The TokenDTO containing the generated JWT token.
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping()
    public TokenDTO generateToken(@RequestBody AuthRequestDTO authRequestDTO) {
        return jwtAuthService.generateJwtToken(authRequestDTO.username(), authRequestDTO.password());
    }

    /**
     * Generates a JWT token for an account based on an existing token.
     *
     * @param token The existing JWT token passed in the Authorization header.
     * @return The TokenDTO containing the generated JWT token.
     * @throws GeneralSecurityException If a security exception occurs during token generation.
     * @throws IOException              If an I/O exception occurs.
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/account")
    public TokenDTO generateTokenForAccount(@RequestHeader("Authorization") String token) throws GeneralSecurityException, IOException {
        return externalJwtTokenAuthService.generateJwtTokenByGoogleToken(token);
    }

    /**
     * Validates a JWT token for a given username.
     *
     * @param username The username for which the token needs to be validated.
     * @param token    The JWT token passed in the Authorization header.
     * @return The ValidatorDTO indicating whether the token is valid or not.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{username}")
    public ValidatorDTO validateToken(@PathVariable String username, @RequestHeader("Authorization") String token) {
        return new ValidatorDTO(jwtAuthService.validateToken(token, username));
    }

    /**
     * Refreshes a JWT token based on the provided TokenDTO.
     *
     * @param tokenDTO The TokenDTO containing the username for token refresh.
     * @return The TokenDTO containing the refreshed JWT token.
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping()
    public TokenDTO refreshToken(@RequestBody TokenDTO tokenDTO) {
        return jwtAuthService.generateJwtToken(tokenDTO.username());
    }
}
