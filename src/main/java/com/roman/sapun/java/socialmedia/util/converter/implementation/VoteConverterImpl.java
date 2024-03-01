package com.roman.sapun.java.socialmedia.util.converter.implementation;

import com.roman.sapun.java.socialmedia.dto.FileDTO;
import com.roman.sapun.java.socialmedia.dto.VoteDTO;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.util.ImageUtil;
import com.roman.sapun.java.socialmedia.util.converter.VoteConverter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VoteConverterImpl implements VoteConverter {
    private final ImageUtil imageUtil;

    public VoteConverterImpl(ImageUtil imageUtil) {
        this.imageUtil = imageUtil;
    }

    @Override
    public List<VoteDTO> convertToVoteDTO(Set<UserEntity> users) {
        return users.stream()
                .map(user ->
                        new VoteDTO(user, new FileDTO(user.getImage(), imageUtil.decompressImage(user.getImage().getImageData()))))
                .collect(Collectors.toList());
    }
}
