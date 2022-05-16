package co.axelrod.reactivechat.controller;

import co.axelrod.reactivechat.model.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MessageControllerIntgTest {
    public static final String REACTIVE_CHAT_URL = "/v1/reactivechat";

    @Autowired
    WebTestClient webTestClient;

    @Test
    void sendMessage() {
        // given
        var message = Message.builder()
                .username("vadim")
                .message("hello")
                .build();

        // when
        webTestClient
                .post()
                .uri(REACTIVE_CHAT_URL + "/send")
                .bodyValue(message)
                .exchange()
                .expectStatus().isAccepted();

        // then
    }

    @Test
    void messagesStream() {
        // given
        var message = Message.builder()
                .username("vadim")
                .message("hello")
                .build();

        // when
        webTestClient
                .post()
                .uri(REACTIVE_CHAT_URL + "/send")
                .bodyValue(message)
                .exchange()
                .expectStatus().isAccepted();

        var messageStreamFlux = webTestClient
                .get()
                .uri(REACTIVE_CHAT_URL + "/messages")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Message.class)
                .getResponseBody();

        // then
        StepVerifier.create(messageStreamFlux)
                .assertNext(receivedMessage -> {
                    assert receivedMessage != null;
                    assertEquals("vadim", receivedMessage.getUsername());
                    assertEquals("hello", receivedMessage.getMessage());
                })
                .thenCancel()
                .verify();
    }
}
