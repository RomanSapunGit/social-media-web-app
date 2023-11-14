package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.credentials.AuthRequestDTO;
import com.roman.sapun.java.socialmedia.dto.credentials.ResetPassDTO;
import com.roman.sapun.java.socialmedia.dto.credentials.SignUpDTO;
import com.roman.sapun.java.socialmedia.dto.credentials.TokenDTO;
import com.roman.sapun.java.socialmedia.dto.user.RequestUserDTO;
import com.roman.sapun.java.socialmedia.exception.InvalidValueException;
import com.roman.sapun.java.socialmedia.exception.TokenExpiredException;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import com.roman.sapun.java.socialmedia.exception.ValuesAreNotEqualException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

public interface AuthenticationService {
    /**
     * <p>Adds new User to the database and returns it as a DTO object.</p>
     *
     * @param signUpDto - User details, including name, username, email and password.
     * @param image User's image that will be bound to new user.
     * @return User's details excluding password.
     * @throws IOException if adding image is failed.
     */
    RequestUserDTO register(SignUpDTO signUpDto, MultipartFile image, HttpServletRequest request, HttpServletResponse response) throws IOException, InvalidValueException, UserNotFoundException;

    AuthRequestDTO loginUser(AuthRequestDTO authRequestDTO, HttpServletRequest request, HttpServletResponse response) throws InvalidValueException;

    TokenDTO loginUser(String token, HttpServletRequest request, HttpServletResponse response) throws UserNotFoundException, GeneralSecurityException, IOException, InvalidValueException;


    boolean validateSession(HttpSession httpSession);

    /**
     * <p>Resets User's password.</p>
     * @param token for confirming user.
     * @param resetPassDTO includes 2 passwords for matching and updating it.
     * @return DTO object that include passwords.
     * @throws TokenExpiredException if difference between token creation and current date is more than 24 hours.
     * @throws ValuesAreNotEqualException if passwords in DTO object doesn't match.
     */
     ResetPassDTO resetPassword(String token, ResetPassDTO resetPassDTO) throws TokenExpiredException, ValuesAreNotEqualException, UserNotFoundException;

    /**
     * <p>Sends email to User email.</p>
     * @param email where to send.
     */
     void sendEmail(String email) throws MessagingException, UnsupportedEncodingException, UserNotFoundException;

    /**
     * <p>Check if user is authenticated.</p>
     * @param username User's username.
     * @param password User's password.
     * @return is User authenticated or not.
     */
    boolean checkUserByCredentials(String username, String password);

}
