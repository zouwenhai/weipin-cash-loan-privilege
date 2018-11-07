package nirvana.cash.loan.privilege.websocket;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.contants.RedisKeyContant;
import nirvana.cash.loan.privilege.service.base.RedisService;
import nirvana.cash.loan.privilege.websocket.facade.WebSocketMsgNotice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author dongdong
 * @date 2018/11/7
 */
@Slf4j
@Component
public class MessageContainer {

    @Autowired
    private RedisService redisService;

    public String getMessageNeedToSendByUser(String userId) {
        String key = RedisKeyContant.YOFISHDK_MSG_NOTICE_PREFIX + userId, messageContent = null;
        Set<String> userMessageSet = null;
        try {
            userMessageSet = redisService.getSet(key);
        } catch (Exception e) {
            log.error("从redis中获取用户的消息失败！" + e.getMessage(), e);
        }
        if (!CollectionUtils.isEmpty(userMessageSet)) {
            //取一条，返回给客户端
            String messageValue = userMessageSet.stream().findFirst().get();
            try {
                redisService.remove(key, new String[]{messageValue});
            } catch (Exception e) {
                log.error("从redis中移除用户的消息失败！" + e.getMessage(), e);
            }
            WebSocketMsgNotice webSocketMsgNotice = JSONObject.parseObject(messageValue, WebSocketMsgNotice.class);
            messageContent = webSocketMsgNotice.getMsg();
        }
        return messageContent;
    }

}
