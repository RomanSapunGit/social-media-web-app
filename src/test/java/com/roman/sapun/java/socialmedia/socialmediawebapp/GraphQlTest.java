package com.roman.sapun.java.socialmedia.socialmediawebapp;

import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.roman.sapun.java.socialmedia.SocialMediaJavaApplication;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import io.micrometer.core.instrument.util.IOUtils;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.io.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SocialMediaJavaApplication.class)
@RunWith(SpringRunner.class)
public class GraphQlTest {
    private static final String GRAPHQL_QUERY_REQUEST_PATH = "graphql/%s.graphql";
    private static final String GRAPHQL_QUERY_RESPONSE_PATH = "graphql/%s.json";
    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Test
    @Ignore
    public void testGetPosts()  {
        var testName = "post";
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        IntStream.range(0, 1000).forEach(i -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    graphQLTestTemplate.postForResource(String.format(GRAPHQL_QUERY_REQUEST_PATH, testName));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            futures.add(future);
        });
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).orTimeout(90, TimeUnit.SECONDS).join();
    }

    @Test
    public void shouldGetPosts() throws IOException, JSONException {
        var testName = "post";
        var response = graphQLTestTemplate.postForResource(String.format(GRAPHQL_QUERY_REQUEST_PATH, testName));

        var expectedResponseBody = read(String.format(GRAPHQL_QUERY_RESPONSE_PATH, testName));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertEquals(expectedResponseBody, response.getRawResponse().getBody(), true);
    }

    private String read(String location) throws IOException {
        return IOUtils.toString(new ClassPathResource(location).getInputStream(), StandardCharsets.UTF_8);
    }
}
