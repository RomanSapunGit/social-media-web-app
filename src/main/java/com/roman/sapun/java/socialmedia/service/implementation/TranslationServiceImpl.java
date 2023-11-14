package com.roman.sapun.java.socialmedia.service.implementation;

import com.roman.sapun.java.socialmedia.config.ValueConfig;
import com.roman.sapun.java.socialmedia.dto.translation.TranslationRequestDTO;
import com.roman.sapun.java.socialmedia.dto.translation.TranslationResponseDTO;
import com.roman.sapun.java.socialmedia.exception.TranslationFailedException;
import com.roman.sapun.java.socialmedia.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class TranslationServiceImpl implements TranslationService {
    private final ValueConfig valueConfig;
    @Autowired
    public TranslationServiceImpl(ValueConfig valueConfig) {
        this.valueConfig = valueConfig;
    }

    @Override
    public TranslationResponseDTO translateText(String text, String targetLanguage) throws TranslationFailedException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        TranslationRequestDTO request = new TranslationRequestDTO(text, "auto", targetLanguage, "text", "");
        HttpEntity<TranslationRequestDTO> entity = new HttpEntity<>(request, headers);
        ResponseEntity<TranslationResponseDTO> responseEntity = restTemplate.postForEntity(valueConfig.getLibreTranslateUrl() + "/translate", entity, TranslationResponseDTO.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        } else {
            throw new TranslationFailedException();
        }
    }
}
