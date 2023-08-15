package com.roman.sapun.java.socialmedia.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.UriComponents;

public interface URLBuilder {
    /**
     * Builds a URL for the "reset-password" endpoint with the provided token.
     *
     * @param token The token to include in the URL as a query parameter.
     * @return The built URL as a UriComponents object.
     */
    UriComponents buildUrl( String token);
}
