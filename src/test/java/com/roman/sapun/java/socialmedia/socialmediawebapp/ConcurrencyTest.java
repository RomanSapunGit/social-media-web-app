package com.roman.sapun.java.socialmedia.socialmediawebapp;

import com.roman.sapun.java.socialmedia.dto.post.RequestPostDTO;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * The ConcurrencyTest class is a set of JUnit tests designed to evaluate the concurrency
 * and performance aspects of specific functionalities within the application. It utilizes
 * the Spring Framework and MockMvc for testing the web layer. The tests focus on endpoints
 * related to post retrieval and post creation, simulating concurrent user interactions.
 * <p>
 * Note: The class is annotated with @WebAppConfiguration and @SpringBootTest, indicating
 * the configuration for a web application and specifying that the entire application
 * context should be loaded during testing.
 */
@WebAppConfiguration
@SpringBootTest
@RunWith(SpringRunner.class)
public class ConcurrencyTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private UserDetailsService userDetailsService;

    private MockMvc mockMvc;

    /**
     * Set up the MockMvc instance with the WebApplicationContext and Spring Security configuration.
     */

    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    /**
     * Test the concurrent retrieval of posts using MockMvc. The test sends multiple
     * simultaneous requests to the "/api/v1/post/search" endpoint and measures the
     * time it takes to complete the requests.
     */
    @Test
    @Ignore
    public void testGetPostsConcurrency() {
        setUp();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        var userDetails = userDetailsService.loadUserByUsername("Admin1");
        long start = System.currentTimeMillis();
        IntStream.range(1, 1000).forEach(i -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post/search")
                                    .param("page", "0")
                                    .param("pageSize", "25")
                                    .param("sortBy", "creationTime")
                                    .with(user(userDetails)))
                            .andExpect(status().isOk());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                }
            });
            futures.add(future);
        });

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).orTimeout(120, TimeUnit.SECONDS).join();

        long end = System.currentTimeMillis();
        System.err.println((end - start) + " ms");
    }

    /**
     * Test the concurrent creation of posts using MockMvc and multipart file upload.
     * Test creates several images along with a dto for request,
     * then sends multiple simultaneous requests to the "/api/v1/post" endpoint
     * with multipart file data and measures the time it takes to complete the requests.
     *
     * @throws IOException If an I/O exception occurs during file reading.
     */
    @Test
    public void testCreatePostConcurrency() throws IOException {
        setUp();
        RequestPostDTO requestPostDTO = new RequestPostDTO("identifier", "title", "description");
        var userDetails = userDetailsService.loadUserByUsername("Admin1");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
            byte[] imageBytes = Files.readAllBytes(Paths.get("src/main/resources/images/test.png"));
            var image = new MockMultipartFile("images", "filename.txt", "text/plain", imageBytes);
        var image1 = new MockMultipartFile("images", "filename.txt", "text/plain", imageBytes);
        var image2 = new MockMultipartFile("images", "filename.txt", "text/plain", imageBytes);
        long start = System.currentTimeMillis();
        IntStream.range(1, 1000).forEach(i -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/post")
                                    .file(image)
                                    .file(image1)
                                    .file(image2)
                                    .param("identifier", requestPostDTO.identifier())
                                    .param("title", requestPostDTO.title())
                                    .param("description", requestPostDTO.description())
                                    .contentType(MediaType.MULTIPART_FORM_DATA)
                                    .with(user(userDetails)))
                            .andExpect(status().isCreated());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                }
            });
            futures.add(future);
        });
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).orTimeout(69, TimeUnit.SECONDS).join();
        long end = System.currentTimeMillis();
        System.err.println((end - start) + " ms");
    }
}