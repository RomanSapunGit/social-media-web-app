package com.roman.sapun.java.socialmedia.util.converter;

import org.springframework.data.domain.Page;

import java.util.Map;

public interface PageConverter {
    <T> Map<String, Object> convertPageToResponse(final Page<T> pagePersons);
}
