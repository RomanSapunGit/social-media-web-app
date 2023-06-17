package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.ResetPassDTO;
import com.roman.sapun.java.socialmedia.dto.SignUpDTO;
import com.roman.sapun.java.socialmedia.dto.UserDTO;
import com.roman.sapun.java.socialmedia.exception.TokenExpiredException;
import com.roman.sapun.java.socialmedia.exception.ValuesAreNotEqualException;
import com.roman.sapun.java.socialmedia.service.CredentialsService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/account")
public class CredentialsController {

    private final CredentialsService credentialsService;

    @Autowired
    public CredentialsController(CredentialsService credentialsService) {
        this.credentialsService = credentialsService;
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
}
