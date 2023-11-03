package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.post.ResponsePostDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/sse")
public class SSEController {
    private final Map<String, Sinks.Many<ServerSentEvent<String>>> userSinks = new ConcurrentHashMap<>();
    private final Map<String, Sinks.Many<ServerSentEvent<ResponsePostDTO>>> updateSinks = new ConcurrentHashMap<>();

    /**
     * Retrieves real-time notifications for a specific user identified by their username
     * using Server-Sent Events (SSE).
     *
     * @param username The username of the user for whom notifications are to be retrieved.
     * @return A Flux of ServerSentEvent<String> representing real-time notifications for the user.
     */
    @GetMapping("/notifications")
    public Flux<ServerSentEvent<String>> getNotifications(@RequestParam String username) {
        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().unicast().onBackpressureBuffer();
        userSinks.put(username, sink);
        Flux<Long> heartbeat = Flux.interval(Duration.ofSeconds(30));
        Flux<ServerSentEvent<String>> sseWithHeartbeat = Flux.merge(
                sink.asFlux(),
                heartbeat.map(seq -> ServerSentEvent.builder(":heartbeat").build())
        );
        return sseWithHeartbeat
                .doFinally(signalType -> {
                    if (signalType == SignalType.ON_COMPLETE) {
                        userSinks.remove(username);
                    }
                });
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/notifications/complete")
    public void completeConnection(@RequestParam String username) {
        userSinks.remove(username);
    }

    /**
     * Retrieves real-time updates for a specific post identified by its postId
     * using Server-Sent Events (SSE).
     *
     * @param postId The unique identifier of the post for which updates are to be retrieved.
     * @return A Flux of ServerSentEvent<ResponsePostDTO> representing real-time post updates.
     */
    @GetMapping("/posts/updates")
    public Flux<ServerSentEvent<ResponsePostDTO>> getUpdates(@RequestParam String postId) {
        Sinks.Many<ServerSentEvent<ResponsePostDTO>> sink = Sinks.many().unicast().onBackpressureBuffer();
        updateSinks.put(postId, sink);
        return sink.asFlux()
                .doFinally(signalType -> {
                    if (signalType == SignalType.ON_COMPLETE) {
                        updateSinks.remove(postId);
                    }
                });
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/posts/updates/complete")
    public void completePostUpdateConnection(@RequestParam String postId) {
        updateSinks.remove(postId);
    }

    /**
     * Sends a real-time update for a specific post identified by its postId
     * to clients subscribed to the post's updates using Server-Sent Events (SSE).
     *
     * @param postId The unique identifier of the post to which the update belongs.
     * @param post   The updated post represented as a ResponsePostDTO.
     */
    public void sendPostUpdate(String postId, ResponsePostDTO post) {
        var postSink = updateSinks.get(postId);
        if (postSink != null) {
            ServerSentEvent<ResponsePostDTO> event = ServerSentEvent.builder(post).build();
            postSink.tryEmitNext(event);
        }
    }

    /**
     * Sends a real-time notification to a specific user identified by their userId
     * using Server-Sent Events (SSE).
     *
     * @param username     The unique username of the user to whom the notification is sent.
     * @param notification The notification message to be sent.
     */
    public void sendNotification(String username, String notification) {
        var userSink = userSinks.get(username);
        if (userSink != null) {
            ServerSentEvent<String> event = ServerSentEvent.builder(notification).build();
            userSink.tryEmitNext(event);
        }
    }
}
