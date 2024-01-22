package com.roman.sapun.java.socialmedia.config;

public interface ValueConfig {
     String getEmailSubject();
     String getUrl();
     String getClientId();
     int getPageSize();

     int getTwelveHours();

    String getSlackWebhookUrl();

    String getLibreTranslateUrl();
}
