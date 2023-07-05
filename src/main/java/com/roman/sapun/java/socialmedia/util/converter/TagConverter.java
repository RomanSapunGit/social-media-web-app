package com.roman.sapun.java.socialmedia.util.converter;

import com.roman.sapun.java.socialmedia.entity.TagEntity;

public interface TagConverter {


    TagEntity convertToTagEntity(String name);
}
