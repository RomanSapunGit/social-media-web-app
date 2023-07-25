package com.roman.sapun.java.socialmedia.service.implementation;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.roman.sapun.java.socialmedia.config.ValueConfig;
import com.roman.sapun.java.socialmedia.dto.credentials.TokenDTO;
import com.roman.sapun.java.socialmedia.repository.UserRepository;
import com.roman.sapun.java.socialmedia.service.CredentialsService;
import com.roman.sapun.java.socialmedia.service.JwtAuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtAuthServiceImpl implements JwtAuthService {
    private final CredentialsService credentialsService;

    private final ValueConfig valueConfig;
    private static final String INVALID_USER_REQUEST = "invalid user request";
    private final GsonFactory gsonFactory;
    private final UserRepository userRepository;


    @Autowired
    public JwtAuthServiceImpl(CredentialsService credentialsService, ValueConfig valueConfig, GsonFactory gsonFactory,
                              UserRepository userRepository) {
        this.gsonFactory = gsonFactory;
        this.credentialsService = credentialsService;
        this.valueConfig = valueConfig;
        this.userRepository = userRepository;
    }

    @Override
    public TokenDTO generateJwtToken(String username, String password) {
        if (!(credentialsService.checkUserByCredentials(username, password))) {
            throw new UsernameNotFoundException(INVALID_USER_REQUEST);
        }
        Map<String, Object> claims = new HashMap<>();
        return new TokenDTO(createToken(claims, username), username);
    }

    @Override
    public TokenDTO generateJwtToken(String username) {
        if (username == null) {
            throw new UsernameNotFoundException(INVALID_USER_REQUEST);
        }
        Map<String, Object> claims = new HashMap<>();
        return new TokenDTO(createToken(claims, username), username);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsFunction) {
        var claims = extractAllClaims(token);
        return claimsFunction.apply(claims);
    }

    @Override
    public Boolean isJwtTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public Boolean validateToken(String token, String usernameToMatch) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        var username = extractUsername(token);
        return (username.equalsIgnoreCase(usernameToMatch) && !isJwtTokenExpired(token));
    }

    @Override
    public String validateAndGetUsername(String authToken) throws GeneralSecurityException, IOException {
        if (authToken != null && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
        }

        var email = validateTokenViaGoogleAndGetEmail(authToken);
        if (email == null) {
            return null;
        }
        return userRepository.findByEmail(email).getUsername();
    }

    @Override
    public TokenDTO generateJwtTokenByAnotherToken(String authToken) throws GeneralSecurityException, IOException {
        if (authToken != null && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
        }
        var email = validateTokenViaGoogleAndGetEmail(authToken);
        return generateJwtToken(userRepository.findByEmail(email).getUsername());
    }

    @Override
    public String validateTokenViaGoogleAndGetEmail(String authToken) throws GeneralSecurityException, IOException {
        var httpTransport = new com.google.api.client.http.javanet.NetHttpTransport();
        var verifier = new GoogleIdTokenVerifier.Builder(httpTransport, gsonFactory)
                .setAudience(Collections.singletonList(valueConfig.getClientId()))
                .build();
        var idToken = verifier.verify(authToken);
        if (idToken != null) {
            var payload = idToken.getPayload();
            return (String) payload.get("email");
        }
        return null;
    }

    private Claims extractAllClaims(String token) throws ExpiredJwtException {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 *30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        var keyBytes = Decoders.BASE64.decode(valueConfig.getBase64Code());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
