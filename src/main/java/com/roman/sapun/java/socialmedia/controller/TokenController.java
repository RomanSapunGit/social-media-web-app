package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.AuthRequestDTO;
import com.roman.sapun.java.socialmedia.dto.TokenDTO;
import com.roman.sapun.java.socialmedia.dto.ValidatorDTO;
import com.roman.sapun.java.socialmedia.service.JwtAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/token")
public class TokenController {

    private final JwtAuthService jwtAuthService;

    @Autowired
    public TokenController(JwtAuthService jwtAuthService) {
        this.jwtAuthService = jwtAuthService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping()
    public TokenDTO generateToken(@RequestBody AuthRequestDTO authRequestDTO) {
        return jwtAuthService.generateJwtToken(authRequestDTO.username(), authRequestDTO.password());
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/account")
    public TokenDTO generateTokenForAccount(@RequestHeader("Authorization") String token) throws GeneralSecurityException, IOException {
        return jwtAuthService.generateJwtTokenByAnotherToken(token);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{username}")
    public ValidatorDTO validateToken(@PathVariable String username, @RequestHeader("Authorization") String token) {
        return new ValidatorDTO(jwtAuthService.validateToken(token, username));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping()
    public TokenDTO refreshToken(@RequestBody TokenDTO tokenDTO) {
        return jwtAuthService.generateJwtToken(tokenDTO.username());
    }
}
