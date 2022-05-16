package co.axelrod.reactivechat.controller;

import co.axelrod.reactivechat.model.Message;
import co.axelrod.reactivechat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/reactivechat")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> sendMessage(@RequestBody Message message) {
        messageService.sendMessage(message);
        return Mono.empty();
    }

    @GetMapping(value = "/messages", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Message> messagesStream() {
        return messageService.getMessagesSink().asFlux();
    }
}
