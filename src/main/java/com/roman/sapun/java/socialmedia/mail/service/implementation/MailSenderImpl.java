package com.roman.sapun.java.socialmedia.mail.service.implementation;

import com.roman.sapun.java.socialmedia.mail.config.EmailAddresser;
import com.roman.sapun.java.socialmedia.mail.service.MailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;

import java.io.UnsupportedEncodingException;

@Component
public class MailSenderImpl implements MailSender {
    private static final String SUBJECT_TO_MAIL = "Here's the link to reset your password";
    private static final String CONTENT_FIRST_PART = """
            Hello,
            You have requested to reset your password.
            Click the link below to change your password:""";
    private static final String CONTENT_SECOND_PART = """
            Ignore this email if you do remember your password,
            or you have not made the request.""";
    private final JavaMailSender javaMailSender;

    private final EmailAddresser emailAddresser;

    public MailSenderImpl( JavaMailSender javaMailSender, EmailAddresser emailAddresser) {
        this.javaMailSender = javaMailSender;
        this.emailAddresser = emailAddresser;
    }
    public void sendEmail(String email, UriComponents uri) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(emailAddresser.getEmail(), "Support");
        helper.setTo(email);
        helper.setSubject(SUBJECT_TO_MAIL);
        helper.setText(CONTENT_FIRST_PART + uri + " " + CONTENT_SECOND_PART, true);
        javaMailSender.send(message);
    }
}
