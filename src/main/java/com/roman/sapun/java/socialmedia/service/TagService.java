package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.page.TagPageDTO;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.TagEntity;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TagService {
    /**
     * Retrieves a paginated list of tags.
     *
     * @param page The page number to retrieve.
     * @return map containing 10 tags, overall number of comments, current comment page and overall number of pages.
     */ //TODO rewrite the documentation
    Page<TagEntity> getTags(int page, int pageSize);



    /**
     * Extracts existing tags from the given text.
     *
     * @param text The text to extract tags from.
     * @return A set of existing in database tag entities found in the text.
     */
    Set<TagEntity> getExistingTagsFromText(String text);

    Page<TagEntity> getExistingTagsFromText(String text, int pageSize, int page);

    TagPageDTO getExistingTagsByText(String text, int pageSize, int page);

    /**
     * Extracts non-existing tags from the given text and saves them in the database.
     *
     * @param text The text to extract tags from.
     * @return A set of new tag entities.
     */
    Set<TagEntity> saveNonExistingTagsFromText(String text);

    Mono<Map<PostEntity, Set<TagEntity>>> getBatchedTags(List<PostEntity> posts);
}
