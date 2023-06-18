package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.AuthRequestDTO;
import com.roman.sapun.java.socialmedia.dto.ResetPassDTO;
import com.roman.sapun.java.socialmedia.dto.SignUpDTO;
import com.roman.sapun.java.socialmedia.dto.UserDTO;
import com.roman.sapun.java.socialmedia.exception.TokenExpiredException;
import com.roman.sapun.java.socialmedia.exception.ValuesAreNotEqualException;
import com.roman.sapun.java.socialmedia.service.CredentialsService;
import com.roman.sapun.java.socialmedia.service.JwtAuthService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/account")
public class CredentialsController {

    private final CredentialsService credentialsService;
    private final AuthenticationManager authenticationManager;
    private final JwtAuthService jwtAuthService;

    @Autowired
    public CredentialsController(CredentialsService credentialsService, AuthenticationManager authenticationManager,
                                 JwtAuthService jwtAuthService) {
        this.credentialsService = credentialsService;
        this.authenticationManager = authenticationManager;
        this.jwtAuthService = jwtAuthService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public UserDTO registerUser(@RequestBody SignUpDTO signUpDto) {
        return credentialsService.addNewUser(signUpDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{email}")
    public void forgotPassword(@PathVariable String email, HttpServletRequest request) throws MessagingException,
            UnsupportedEncodingException {
        credentialsService.sendEmail(email, request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{token}")
    public ResetPassDTO resetPassword(@PathVariable String token, @RequestBody ResetPassDTO resetPassDTO) throws TokenExpiredException, ValuesAreNotEqualException {
        return credentialsService.resetPassword(token, resetPassDTO);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/authentication")
    public String authenticate(@RequestBody AuthRequestDTO authRequestDTO) {
        Authentication authentication = authenticationManager.authenticate
                (new UsernamePasswordAuthenticationToken(authRequestDTO.username(), authRequestDTO.password()));
        if (authentication.isAuthenticated()) {
            return jwtAuthService.generateJwtToken(authRequestDTO.username());
        } else {
            throw new UsernameNotFoundException("invalid user request");
        }
    }
}
