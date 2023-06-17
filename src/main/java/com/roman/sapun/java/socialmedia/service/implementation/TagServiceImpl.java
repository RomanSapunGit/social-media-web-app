package com.roman.sapun.java.socialmedia.service.implementation;

import com.roman.sapun.java.socialmedia.entity.TagEntity;
import com.roman.sapun.java.socialmedia.repository.TagRepository;
import com.roman.sapun.java.socialmedia.service.TagService;
import com.roman.sapun.java.socialmedia.util.TagConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagConverter tagConverter;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, TagConverter tagConverter) {
        this.tagRepository = tagRepository;
        this.tagConverter = tagConverter;
    }

    @Override
    public Set<TagEntity> getExistingTagsFromText(String text) {
        Set<String> tagSet = extractTagsFromText(text);
        return tagSet.stream()
                .map(tagRepository::findByName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<TagEntity> saveNonExistingTagsFromText(String text) {
        Set<String> tagSet = extractTagsFromText(text);
        return tagSet.stream()
                .filter(tagEntity -> !tagRepository.existsByName(tagEntity))
                .map(tagConverter::convertToTagEntity)
                .map(tagRepository::save)
                .collect(Collectors.toSet());
    }

    private Set<String> extractTagsFromText(String text) {
        Set<String> hashtags = new HashSet<>();
        Pattern pattern = Pattern.compile("#\\w+");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String hashtag = matcher.group();
            hashtags.add(hashtag);
        }
        return hashtags;
    }
}
