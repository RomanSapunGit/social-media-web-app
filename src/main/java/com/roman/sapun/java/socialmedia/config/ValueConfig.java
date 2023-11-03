package com.roman.sapun.java.socialmedia.config;

public interface ValueConfig {
     String getBase64Code();
     String getEmailSubject();
     String getUrl();
     String getClientId();
     int getPageSize();

     int getTwelveHours();

    String getSlackWebhookUrl();

    String getLibreTranslateUrl();
}
