package nirvana.cash.loan.privilege.websocket.hook;

import nirvana.cash.loan.privilege.websocket.facade.WebSocketMsgNoticeFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author dongdong
 * @date 2018/11/9
 */
@RestController
public class WebSocketMessageHook {

    public static final String MESSAGE_SEND_API = "/privilege/notauth/webSocket/send";

    @Autowired
    private WebSocketMessageSender webSocketMessageSender;

    @PostMapping(path = MESSAGE_SEND_API)
    public void sendMessageToUser(@RequestBody WebSocketMsgNoticeFacade messageFacade) {
        Long userId = messageFacade.getUserId();
        if (userId != null) {
            webSocketMessageSender.doSend(String.valueOf(userId), messageFacade.getMsg());
        }
    }

    @GetMapping(path = "/test/{userId}/{message}")
    public String testSend(@PathVariable String userId, @PathVariable String message) {
        webSocketMessageSender.sendMessageToClient(Long.valueOf(userId), message);
        return "xxx";
    }

}
