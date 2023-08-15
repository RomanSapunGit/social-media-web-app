package com.roman.sapun.java.socialmedia.util.mail;

import com.roman.sapun.java.socialmedia.config.ValueConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final JavaMailSender javaMailSender;

    private final ValueConfig valueConfig;

    @Autowired
    public MailSenderImpl(JavaMailSender javaMailSender, ValueConfig valueConfig) {
        this.javaMailSender = javaMailSender;
        this.valueConfig = valueConfig;
    }

    @Override
    public void sendEmail(String email, UriComponents uri) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(valueConfig.getEmailSubject(), "Support");
        helper.setTo(email);
        helper.setSubject(SUBJECT_TO_MAIL);
        helper.setText(CONTENT_FIRST_PART + uri + " ", true);
        javaMailSender.send(message);
    }
}
