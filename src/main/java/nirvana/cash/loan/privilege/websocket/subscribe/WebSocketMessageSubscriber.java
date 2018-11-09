package nirvana.cash.loan.privilege.websocket.subscribe;

import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.websocket.handler.WebSocketMessageHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.EmitterProcessor;

/**
 * @author dongdong
 * @date 2018/11/8
 */
@Slf4j
public class WebSocketMessageSubscriber {

    private WebSocketSession webSocketSession;
    private EmitterProcessor processor;
    private String userId;
    private WebSocketMessageHandler handler;

    public WebSocketMessageSubscriber(WebSocketSession webSocketSession, EmitterProcessor processor, String userId, WebSocketMessageHandler handler) {
        this.webSocketSession = webSocketSession;
        this.processor = processor;
        this.userId = userId;
        this.handler = handler;
    }

    public void onError(Throwable e) {
        log.error("webSocket连接出错！", e);
    }

    public void onComplete() {
        log.info("webSocket连接关闭，sessionId:{}", webSocketSession.getId());
        handler.removeSessionProcessor(userId, processor);
        processor = null;
        handler = null;
        webSocketSession.close();
        webSocketSession = null;
    }

    public void onNext(WebSocketMessage webSocketMessage) {
        log.info("客户端有数据通过webSocket推送过来了,数据：{}", webSocketMessage.getPayloadAsText());
    }

}
