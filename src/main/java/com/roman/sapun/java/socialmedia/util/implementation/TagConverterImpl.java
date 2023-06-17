package com.roman.sapun.java.socialmedia.util.implementation;

import com.roman.sapun.java.socialmedia.entity.TagEntity;
import com.roman.sapun.java.socialmedia.util.TagConverter;
import org.springframework.stereotype.Component;

@Component
public class TagConverterImpl implements TagConverter {

    @Override
    public TagEntity convertToTagEntity(String name) {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setName(name);
        return tagEntity;
    }
}
