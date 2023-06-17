package com.roman.sapun.java.socialmedia.mail.service;

import jakarta.mail.MessagingException;
import org.springframework.web.util.UriComponents;

import java.io.UnsupportedEncodingException;

public interface MailSender {
     void sendEmail(String email, UriComponents uri) throws MessagingException, UnsupportedEncodingException;
}
