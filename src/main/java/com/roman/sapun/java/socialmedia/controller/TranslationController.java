package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.translation.TranslationResponseDTO;
import com.roman.sapun.java.socialmedia.exception.TranslationFailedException;
import com.roman.sapun.java.socialmedia.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/translations")
public class TranslationController {
    private final TranslationService translationService;
    @Autowired
    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    /**
     * Translates the specified text to the target language.
     *
     * @param text           The text to be translated.
     * @param targetLanguage The language to which the text should be translated.
     * @return A TranslationResponseDTO containing the translated text.
     * @throws TranslationFailedException if the translation process encounters an error.
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping()
    public TranslationResponseDTO translateText(@RequestParam String text, @RequestParam String targetLanguage) throws TranslationFailedException {
        return translationService.translateText(text, targetLanguage);
    }
}
