package com.roman.sapun.java.socialmedia.util.converter.implementation;

import com.roman.sapun.java.socialmedia.util.converter.PageConverter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class PageConverterImpl implements PageConverter {
    @Override
    public <T> Map<String, Object> convertPageToResponse(Page<T> pagePersons) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("entities", pagePersons.getContent());
        response.put("current-page", pagePersons.getNumber());
        response.put("total-items", pagePersons.getTotalElements());
        response.put("total-pages", pagePersons.getTotalPages());
        return response;
    }
}
