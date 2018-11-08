package nirvana.cash.loan.privilege.websocket.subscribe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

/**
 * @author dongdong
 * @date 2018/11/8
 */
@Slf4j
public class WebSocketMessageSubscriber {

    private WebSocketSession webSocketSession;

    public WebSocketMessageSubscriber(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

    public void onError(Throwable e) {
        log.error("webSocket连接出错！" + e.getMessage(), e);
    }

    public void onComplete() {
        log.info("webSocket连接关闭，sessionId:{}", webSocketSession.getId());
        webSocketSession.close();
    }

    public void onNext(WebSocketMessage webSocketMessage) {
        log.info("客户端有数据通过webSocket推送过来了,数据：{}", webSocketMessage.getPayloadAsText());
    }

}
