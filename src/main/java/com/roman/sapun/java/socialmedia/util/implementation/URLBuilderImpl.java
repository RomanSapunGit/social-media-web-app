package com.roman.sapun.java.socialmedia.util.implementation;

import com.roman.sapun.java.socialmedia.util.URLBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class URLBuilderImpl implements URLBuilder {
    private static final String URL_PATH = "/account/";

    public UriComponents buildUrl(HttpServletRequest request, String token) {
        return UriComponentsBuilder
                .fromHttpUrl(getSiteURL(request))
                .path(URL_PATH)
                .pathSegment(token)
                .build();
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}