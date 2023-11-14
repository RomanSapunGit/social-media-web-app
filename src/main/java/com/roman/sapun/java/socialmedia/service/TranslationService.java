package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.translation.TranslationResponseDTO;
import com.roman.sapun.java.socialmedia.exception.TranslationFailedException;

public interface TranslationService {
    /**
     * Translates the specified text to the target language.
     *
     * @param text           The text to be translated.
     * @param targetLanguage The language to which the text should be translated.
     * @return A TranslationResponseDTO containing the translated text.
     * @throws TranslationFailedException if the translation process encounters an error.
     */
    TranslationResponseDTO translateText(String text, String targetLanguage) throws TranslationFailedException;
}
