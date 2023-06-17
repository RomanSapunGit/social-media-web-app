package com.roman.sapun.java.socialmedia.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.UriComponents;

public interface URLBuilder {
    UriComponents buildUrl(HttpServletRequest request, String token);
}
