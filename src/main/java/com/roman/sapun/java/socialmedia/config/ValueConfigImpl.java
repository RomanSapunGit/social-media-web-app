package com.roman.sapun.java.socialmedia.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ValueConfigImpl implements ValueConfig {
    private final String base64Code;
    private final String emailSubject;
    private final String url;
    private final String clientId;
    private static final int PAGE_SIZE = 50;


    public ValueConfigImpl(@Value("${BASE64_CODE}") String base64Code,
                           @Value("${MAIL_USERNAME}") String emailSubject,
                           @Value("${FRONTEND_URL}") String url,
                           @Value("${CLIENT_ID}") String clientId) {
        this.base64Code = base64Code;
        this.emailSubject = emailSubject;
        this.url = url;
        this.clientId = clientId;
    }

    public int getPageSize() {
        return PAGE_SIZE;
    }
}
