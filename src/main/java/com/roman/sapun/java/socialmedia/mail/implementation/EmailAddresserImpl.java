package com.roman.sapun.java.socialmedia.mail.implementation;

import com.roman.sapun.java.socialmedia.mail.EmailAddresser;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class EmailAddresserImpl implements EmailAddresser {
    @Value("${MAIL_USERNAME}")
    private String email;
}