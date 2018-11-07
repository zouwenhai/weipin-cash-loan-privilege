package nirvana.cash.loan.privilege.websocket;

import lombok.extern.slf4j.Slf4j;
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

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        System.out.println(webSocketSession.getHandshakeInfo().getUri());
        String username = getUsernameFromSession(webSocketSession);
        if (!StringUtils.hasText(username)) {
            return webSocketSession.send(Mono.just(webSocketSession.textMessage("")));
        }

        return webSocketSession.send(Flux.<String>generate(sink -> {
            String message = MessageContainer.getOneMessageByUsername(username);
            System.out.println(Thread.currentThread().getName());
            if (StringUtils.hasText(message)) {
                sink.next(message);
            }else{
                System.out.println("没有消息要推送");
                sink.next("");
            }
        }).delayElements(Duration.ofSeconds(10)).map(webSocketSession::textMessage));
    }

    private String getUsernameFromSession(WebSocketSession webSocketSession) {
        return Optional.ofNullable(webSocketSession).map(s -> s.getHandshakeInfo()).map(i -> i.getUri()).map(u -> u.getPath())
                .map(p -> p.substring(p.lastIndexOf("/") + 1)).orElseGet(() -> null);
    }

    @Override
    public List<String> getSubProtocols() {
        return null;
    }

}
