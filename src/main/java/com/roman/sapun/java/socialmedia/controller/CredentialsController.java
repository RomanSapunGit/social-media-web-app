package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.config.ValueConfig;
import com.roman.sapun.java.socialmedia.dto.credentials.ResetPassDTO;
import com.roman.sapun.java.socialmedia.dto.credentials.SignUpDTO;
import com.roman.sapun.java.socialmedia.dto.user.RequestUserDTO;
import com.roman.sapun.java.socialmedia.exception.TokenExpiredException;
import com.roman.sapun.java.socialmedia.exception.ValuesAreNotEqualException;
import com.roman.sapun.java.socialmedia.service.CredentialsService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/v1/account")
public class CredentialsController {

    private final CredentialsService credentialsService;

    @Autowired
    public CredentialsController(CredentialsService credentialsService) {
        this.credentialsService = credentialsService;
    }
    /**
     * Registers a new user.
     *
     * @param signUpDto The DTO containing the sign-up details of the user.
     * @param image     The image file uploaded by the user.
     * @return The DTO representing the registered user.
     * @throws IOException If an error occurs while processing the image file.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public RequestUserDTO registerUser(@ModelAttribute SignUpDTO signUpDto, @RequestPart("image")MultipartFile image) throws IOException {
        return credentialsService.addNewUser(signUpDto, image);
    }
    /**
     * Initiates the process of resetting the user's password.
     *
     * @param email The email address of the user.
     * @throws MessagingException           If an error occurs while sending the password reset email.
     * @throws UnsupportedEncodingException If the email subject encoding is not supported.
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{email}")
    public void forgotPassword(@PathVariable String email) throws MessagingException,
            UnsupportedEncodingException {
        credentialsService.sendEmail(email);
    }
    /**
     * Resets the user's password.
     *
     * @param token        The token associated with the password reset request.
     * @param resetPassDTO The DTO containing the new password and its confirmation.
     * @return The DTO representing the updated password information.
     * @throws TokenExpiredException      If the token has expired.
     * @throws ValuesAreNotEqualException If the new password and its confirmation do not match.
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{token}")
    public ResetPassDTO resetPassword(@PathVariable String token, @RequestBody ResetPassDTO resetPassDTO) throws TokenExpiredException, ValuesAreNotEqualException {
        return credentialsService.resetPassword(token, resetPassDTO);
    }
}
