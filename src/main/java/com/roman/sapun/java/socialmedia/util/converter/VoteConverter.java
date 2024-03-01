package com.roman.sapun.java.socialmedia.util.converter;

import com.roman.sapun.java.socialmedia.dto.VoteDTO;
import com.roman.sapun.java.socialmedia.entity.UserEntity;

import java.util.List;
import java.util.Set;

public interface VoteConverter {
    List<VoteDTO> convertToVoteDTO(Set<UserEntity> users);
}
