package com.roman.sapun.java.socialmedia.util;

import com.roman.sapun.java.socialmedia.dto.TagDTO;
import com.roman.sapun.java.socialmedia.entity.TagEntity;

public interface TagConverter {


    TagEntity convertToTagEntity(String name);
}
