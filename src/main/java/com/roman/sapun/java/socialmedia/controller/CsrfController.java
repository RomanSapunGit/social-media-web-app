package com.roman.sapun.java.socialmedia.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/csrf")

public class CsrfController {
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/token")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }
}
