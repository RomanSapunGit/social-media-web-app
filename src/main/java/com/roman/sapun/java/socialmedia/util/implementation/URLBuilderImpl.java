package com.roman.sapun.java.socialmedia.util.implementation;

import com.roman.sapun.java.socialmedia.config.ValueConfig;
import com.roman.sapun.java.socialmedia.util.URLBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class URLBuilderImpl implements URLBuilder {

    private final ValueConfig valueConfig;

    @Autowired
    public URLBuilderImpl(ValueConfig valueConfig) {
        this.valueConfig = valueConfig;
    }

    @Override
    public UriComponents buildUrl(String token) {
        return UriComponentsBuilder
                .fromHttpUrl(valueConfig.getUrl())
                .pathSegment("reset-password")
                .queryParam("token", token)
                .build();
    }
}