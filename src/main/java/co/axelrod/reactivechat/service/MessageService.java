package co.axelrod.reactivechat.service;

import co.axelrod.reactivechat.model.Message;
import lombok.Getter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

@Component
@Getter
public class MessageService {
    private final Sinks.Many<Message> messagesSink = Sinks.many().replay().all();

    public void sendMessage(Message message) {
        messagesSink.tryEmitNext(message);
    }
}
