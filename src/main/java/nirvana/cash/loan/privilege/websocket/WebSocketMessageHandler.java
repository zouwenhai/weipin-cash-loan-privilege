package nirvana.cash.loan.privilege.websocket;

import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.websocket.config.WebSocketProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * @author dongdong
 * @date 2018/11/5
 */
@Slf4j
@Component
public class WebSocketMessageHandler implements WebSocketHandler {

    @Autowired
    private MessageBasket messageBasket;
    @Autowired
    private WebSocketProperties properties;

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        String userId = getUserIdFromSession(webSocketSession);
        if (!StringUtils.hasText(userId)) {
            return webSocketSession.send(Mono.just(webSocketSession.textMessage("")));
        }
        return webSocketSession.send(Flux.<String>generate(sink -> {
            String message = messageBasket.fetchOneUserMessage(userId);
            if (StringUtils.hasText(message)) {
                sink.next(message);
            } else {
                sink.next("");
            }
        }).delayElements(Duration.ofSeconds(properties.getDelay())).map(webSocketSession::textMessage));
    }

    private String getUserIdFromSession(WebSocketSession webSocketSession) {
        return Optional.ofNullable(webSocketSession).map(s -> s.getHandshakeInfo()).map(i -> i.getUri()).map(u -> u.getPath())
                .map(p -> p.substring(p.lastIndexOf("/") + 1)).orElseGet(() -> null);
    }

    @Override
    public List<String> getSubProtocols() {
        return null;
    }

}
