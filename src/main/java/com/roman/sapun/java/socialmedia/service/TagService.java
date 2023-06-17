package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.entity.TagEntity;

import java.util.Set;

public interface TagService {
    Set<TagEntity> getExistingTagsFromText(String text);

    Set<TagEntity> saveNonExistingTagsFromText(String text);
}
