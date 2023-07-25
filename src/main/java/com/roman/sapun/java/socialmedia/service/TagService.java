package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.tag.TagDTO;
import com.roman.sapun.java.socialmedia.entity.TagEntity;

import java.util.Map;
import java.util.Set;

public interface TagService {
    Map<String, Object> getTags(int page);

    Set<TagEntity> getExistingTagsFromText(String text);

    Set<TagEntity> saveNonExistingTagsFromText(String text);

}
