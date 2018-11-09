package nirvana.cash.loan.privilege.websocket.hook;

import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.websocket.facade.WebSocketMsgNoticeFacade;
import nirvana.cash.loan.privilege.websocket.handler.WebSocketMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.EmitterProcessor;

import java.net.URI;
import java.util.List;

/**
 * @author dongdong
 * @date 2018/11/9
 */
@Slf4j
@Component
public class WebSocketMessageSender {

    @Autowired
    private WebSocketMessageHandler handler;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.application.name:YOFISHDK-CASH-LOAN-PRIVILEGE}")
    private String applicationName;

    /**
     * 发送webSocket消息给web客户端
     *
     * @param userId  指定需要发送给哪个用户的客户端
     * @param message 要发送给客户端的消息JSON字符串
     */
    public void sendMessageToClient(Long userId, String message) {
        List<ServiceInstance> gatewayInstances = discoveryClient.getInstances(applicationName);
        if (!CollectionUtils.isEmpty(gatewayInstances)) {
            StopWatch watch = new StopWatch("WebSocket消息发送时间分析");
            gatewayInstances.forEach(instance -> {
                try {
                    watch.start(instance.getUri().toString());
                    sendToInstance(instance.getUri(), userId, message);
                } catch (Exception e) {
                    log.warn("发送消息到指定的消息中心服务实例失败，该服务实例可能已被关闭，" +
                            "url:{},serviceId:{}", instance.getUri(), instance.getServiceId());
                } finally {
                    watch.stop();
                }
            });
            log.info(watch.prettyPrint());
        }
    }

    private void sendToInstance(URI uri, Long userId, String message) {
        WebSocketMsgNoticeFacade messageFacade = new WebSocketMsgNoticeFacade();
        messageFacade.setUserId(userId);
        messageFacade.setMsg(message);
        restTemplate.postForObject(uri.toString() + WebSocketMessageHook.MESSAGE_SEND_API, messageFacade, String.class);
    }

    public void doSend(String userId, String message) {
        List<EmitterProcessor<String>> processors = handler.getMap().get(userId);
        if (!CollectionUtils.isEmpty(processors)) {
            processors.forEach(p -> {
                try {
                    p.onNext(message);
                } catch (Exception e) {
                    log.error(String.format("通过webSocket发送消息给用户：%s 出现异常！", userId), e);
                }
            });
        }
    }

}
