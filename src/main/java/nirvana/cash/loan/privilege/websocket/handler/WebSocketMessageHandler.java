package nirvana.cash.loan.privilege.websocket.handler;

import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.websocket.subscribe.WebSocketMessageSubscriber;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author dongdong
 * @date 2018/11/5
 */
@Slf4j
@Component
public class WebSocketMessageHandler implements WebSocketHandler {

    private Map<String, List<EmitterProcessor<String>>> map = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        String userId = getUserIdFromSession(webSocketSession);
        EmitterProcessor<String> processor = createAndCacheUserSessionProcessor(userId, map);
        WebSocketMessageSubscriber subscriber = new WebSocketMessageSubscriber(webSocketSession, processor, userId, this);
        webSocketSession.receive().subscribe(subscriber::onNext, subscriber::onError, subscriber::onComplete);
        return webSocketSession.send(processor.map(webSocketSession::textMessage));
    }

    private String getUserIdFromSession(WebSocketSession webSocketSession) {
        return Optional.ofNullable(webSocketSession).map(s -> s.getHandshakeInfo()).map(i -> i.getUri()).map(u -> u.getPath())
                .map(p -> p.substring(p.lastIndexOf("/") + 1)).orElseGet(() -> null);
    }

    private EmitterProcessor<String> createAndCacheUserSessionProcessor(String userId, Map<String, List<EmitterProcessor<String>>> map) {
        List<EmitterProcessor<String>> processors = map.get(userId);
        if (processors == null) {
            synchronized (this) {
                processors = map.get(userId);
                if (processors == null) {
                    processors = new CopyOnWriteArrayList<>();
                    map.put(userId, processors);
                }
            }
        }
        EmitterProcessor<String> processor = EmitterProcessor.create();
        processors.add(processor);
        return processor;
    }

    @Override
    public List<String> getSubProtocols() {
        return null;
    }

    public void removeSessionProcessor(String userId, EmitterProcessor processor) {
        List<EmitterProcessor<String>> processors = this.map.get(userId);
        if (!CollectionUtils.isEmpty(processors)) {
            processors.remove(processor);
            log.info("当前服务实例下，用户：{} 剩余的webSocket连接数为：{}", userId, processors.size());
        }
    }

    public Map<String, List<EmitterProcessor<String>>> getMap() {
        return map;
    }
}
