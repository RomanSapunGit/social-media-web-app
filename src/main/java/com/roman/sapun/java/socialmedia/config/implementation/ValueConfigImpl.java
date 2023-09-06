package com.roman.sapun.java.socialmedia.config.implementation;

import com.roman.sapun.java.socialmedia.config.ValueConfig;
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
    private static final int TWELVE_HOURS = 43200;
    private final String slackWebhookUrl;

    public ValueConfigImpl(@Value("${BASE64_CODE}") String base64Code,
                           @Value("${MAIL_USERNAME}") String emailSubject,
                           @Value("${FRONTEND_URL}") String url,
                           @Value("${CLIENT_ID}") String clientId,
                           @Value("${SLACK_WEBHOOK_URL}") String slackWebhookUrl) {
        this.base64Code = base64Code;
        this.emailSubject = emailSubject;
        this.url = url;
        this.clientId = clientId;
        this.slackWebhookUrl = slackWebhookUrl;
    }

    @Override
    public int getPageSize() {
        return PAGE_SIZE;
    }

    @Override
    public int getTwelveHours() {
        return TWELVE_HOURS;
    }
    @Override
    public String getSlackWebhookUrl() {
        return slackWebhookUrl;
    }
}
