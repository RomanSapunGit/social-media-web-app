package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.ResetPassDTO;
import com.roman.sapun.java.socialmedia.dto.SignUpDTO;
import com.roman.sapun.java.socialmedia.dto.UserDTO;
import com.roman.sapun.java.socialmedia.exception.TokenExpiredException;
import com.roman.sapun.java.socialmedia.exception.ValuesAreNotEqualException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;

public interface CredentialsService {
     UserDTO addNewUser(SignUpDTO signUpDto);
     ResetPassDTO resetPassword(String token, ResetPassDTO resetPassDTO) throws TokenExpiredException, ValuesAreNotEqualException;
     void sendEmail(String email, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException;

    boolean checkUserByCredentials(String username, String password);
}
