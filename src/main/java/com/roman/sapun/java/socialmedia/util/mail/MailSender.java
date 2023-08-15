package com.roman.sapun.java.socialmedia.util.mail;

import jakarta.mail.MessagingException;
import org.springframework.web.util.UriComponents;

import java.io.UnsupportedEncodingException;

public interface MailSender {
     /**
      * Sends an email to the specified email address with the provided URI.
      *
      * @param email The recipient's email address.
      * @param uri   The URI to include in the email content.
      * @throws MessagingException           If an error occurs while sending the email.
      * @throws UnsupportedEncodingException If the email subject encoding is not supported.
      */
     void sendEmail(String email, UriComponents uri) throws MessagingException, UnsupportedEncodingException;
}
