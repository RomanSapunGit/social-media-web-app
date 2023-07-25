package com.roman.sapun.java.socialmedia.dto.tag;

import com.roman.sapun.java.socialmedia.entity.TagEntity;

public record TagDTO(String name) {
    public TagDTO(TagEntity tagEntity) {
        this(tagEntity.getName());
    }
}
