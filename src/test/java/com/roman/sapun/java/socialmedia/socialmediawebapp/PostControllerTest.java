package com.roman.sapun.java.socialmedia.socialmediawebapp;

import com.roman.sapun.java.socialmedia.dto.image.RequestImageDTO;
import com.roman.sapun.java.socialmedia.dto.post.RequestPostDTO;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.exception.PostNotFoundException;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import com.roman.sapun.java.socialmedia.repository.PostRepository;
import com.roman.sapun.java.socialmedia.service.UserService;
import com.roman.sapun.java.socialmedia.service.implementation.PostServiceImpl;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PostControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private PostServiceImpl postServiceImpl;

    @Ignore
    public void testUpdatePost_ThrowsPostNotFoundException() throws UserNotFoundException {
        String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLQoNC70YAg0JjQsNC9IiwiaWF0IjoxNjkyMTk1ODk1LCJleHAiOjE2OTQ3ODc4OTV9.ZE_Fp6mhZzN1h69HPHDu8xR7mvXNxI-JGWSUXwKnt0c";
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getCredentials()).thenReturn(jwtToken);

        RequestPostDTO requestPostDTO = new RequestPostDTO("", "", "");

        List<RequestImageDTO> images = Collections.emptyList();
        List<RequestImageDTO> newImages = Collections.emptyList();

        var postOwner = new UserEntity();
        UserEntity author = new UserEntity();
        PostEntity postEntity = new PostEntity();
        postEntity.setAuthor(author);

        Mockito.when(userService.findUserByAuth(Mockito.any())).thenReturn(postOwner);
        Mockito.when(postRepository.findByIdentifier(Mockito.anyString()).orElseThrow()).thenReturn(postEntity);

        Assertions.assertThrows(PostNotFoundException.class, () ->
                postServiceImpl.updatePost(requestPostDTO.identifier(), requestPostDTO.title(), requestPostDTO.description(), images, newImages, authentication));
    }
}
