package com.roman.sapun.java.socialmedia.util.converter.implementation;

import com.roman.sapun.java.socialmedia.util.converter.PageConverter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class PageConverterImpl implements PageConverter {
    @Override
    public <T> Map<String, Object> convertPageToResponse(Page<?> pageEntities) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("entities", pageEntities.getContent());
        response.put("currentPage", pageEntities.getNumber());
        response.put("total-items", pageEntities.getTotalElements());
        response.put("totalPages", pageEntities.getTotalPages());
        return response;
    }
    @Override
    public <T> Map<String, Object> convertPageToResponse(Page<?> pageEntities, List<?> content) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("entities", content);
        response.put("currentPage", pageEntities.getNumber());
        response.put("total", pageEntities.getTotalElements());
        response.put("totalPages", pageEntities.getTotalPages());
        return response;
    }
}
