package com.roman.sapun.java.socialmedia.dto.translation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


public record TranslationRequestDTO(String q, String source, String target, String format, String api_key) {

}
