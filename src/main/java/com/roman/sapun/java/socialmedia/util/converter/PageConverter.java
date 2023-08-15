package com.roman.sapun.java.socialmedia.util.converter;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface PageConverter {
    <T> Map<String, Object> convertPageToResponse(final Page<?> pagePersons);

    <T> Map<String, Object> convertPageToResponse(Page<?> pageEntities, List<?> content);
}
