package co.axelrod.reactivechat.service;

import co.axelrod.reactivechat.model.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
@ContextConfiguration(classes = {MessageService.class})
class MessageServiceUnitTest {
    @Autowired
    MessageService messageService;

    @Test
    void sendAndReceive() {
        // given
        var message = Message.builder()
                .username("vadim")
                .message("hello")
                .build();

        // when
        Flux<Message> firstConsumer = messageService.getMessagesSink().asFlux();
        Flux<Message> secondConsumer = messageService.getMessagesSink().asFlux();
        messageService.sendMessage(message);

        // then
        StepVerifier.create(firstConsumer)
                .expectNext(message)
                .thenCancel()
                .verify();

        StepVerifier.create(secondConsumer)
                .expectNext(message)
                .thenCancel()
                .verify();
    }

    @Test
    void sendAndReceive_afterSubscribing_multiple() {
        // given
        var firstMessage = Message.builder()
                .username("vadim")
                .message("hello")
                .build();

        var secondMessage = Message.builder()
                .username("vlad")
                .message("hi")
                .build();

        // when
        messageService.sendMessage(firstMessage);

        Flux<Message> firstConsumer = messageService.getMessagesSink().asFlux();
        Flux<Message> secondConsumer = messageService.getMessagesSink().asFlux();

        messageService.sendMessage(secondMessage);

        // then
        StepVerifier.create(firstConsumer)
                .expectNext(firstMessage)
                .thenCancel()
                .verify();

        StepVerifier.create(secondConsumer)
                .expectNext(firstMessage)
                .thenCancel()
                .verify();
    }
}
