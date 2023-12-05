package com.roman.sapun.java.socialmedia.service.implementation;

import com.roman.sapun.java.socialmedia.dto.page.TagPageDTO;
import com.roman.sapun.java.socialmedia.entity.TagEntity;
import com.roman.sapun.java.socialmedia.repository.TagRepository;
import com.roman.sapun.java.socialmedia.service.TagService;
import com.roman.sapun.java.socialmedia.util.converter.PageConverter;
import com.roman.sapun.java.socialmedia.util.converter.TagConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagConverter tagConverter;
    private final PageConverter pageConverter;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, TagConverter tagConverter, PageConverter pageConverter) {
        this.tagRepository = tagRepository;
        this.tagConverter = tagConverter;
        this.pageConverter = pageConverter;
    }

    @Override
    public TagPageDTO getTags(int page, int pageSize) {
        var pageable = PageRequest.of(page, pageSize);
        var tagsPage = tagRepository.findAll(pageable);
        return pageConverter.convertPageToTagPageDTO(tagsPage);
    }


    @Override
    public Set<TagEntity> getExistingTagsFromText(String text) {
        var tagSet = extractTagsFromText(text);
        return tagSet.stream()
                .map(tagRepository::findByNameContaining)
                .filter(Objects::nonNull)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public TagPageDTO getExistingTagsFromText(String text, int pageSize, int page) {
        var pageable = PageRequest.of(page, pageSize);
        var tagsPage = tagRepository.findByNameContaining(text, pageable);
        return pageConverter.convertPageToTagPageDTO(tagsPage);
    }

    @Override
    public Set<TagEntity> saveNonExistingTagsFromText(String text) {
        var tagSet = extractTagsFromText(text);
        return tagSet.stream()
                .filter(tagName -> !tagRepository.existsByName(tagName))
                .map(tagConverter::convertToTagEntity)
                .map(tagRepository::save)
                .collect(Collectors.toSet());
    }

    private Set<String> extractTagsFromText(String text) {
        var hashtags = new HashSet<String>();
        var pattern = Pattern.compile("#\\w+");
        var matcher = pattern.matcher(text);
        while (matcher.find()) {
            String hashtag = matcher.group();
            hashtags.add(hashtag);
        }
        return hashtags;
    }

}
