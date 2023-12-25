package com.roman.sapun.java.socialmedia.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * CsrfController is a Spring REST controller responsible for handling Cross-Site Request Forgery (CSRF) protection.
 * It includes an endpoint for retrieving a CSRF token.
 */
@RestController
@RequestMapping("/api/v1/csrf")
public class CsrfController {

    /**
     * Retrieves a CSRF token.
     *
     * @param token The CSRFToken object representing the CSRF token.
     * @return The CSRFToken object containing the CSRF token.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/token")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }
}