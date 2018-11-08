package nirvana.cash.loan.privilege.websocket;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.contants.RedisKeyContant;
import nirvana.cash.loan.privilege.service.base.RedisService;
import nirvana.cash.loan.privilege.websocket.facade.WebSocketMsgNoticeFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * @author dongdong
 * @date 2018/11/7
 */
@Slf4j
@Component
public class MessageBasket {

    @Autowired
    private RedisService redisService;

    public String fetchOneUserMessage(String userId) {
        String key = RedisKeyContant.YOFISHDK_MSG_NOTICE_PREFIX + userId, messageContent = null;
        Set<String> messagesFromCache = getMessagesFromCache(key);
        if (!CollectionUtils.isEmpty(messagesFromCache)) {
            //取一条，返回给客户端
            String messageValue = messagesFromCache.stream().findFirst().get();
            try {
                redisService.remove(key, new String[]{messageValue});
            } catch (Exception e) {
                log.error("从redis中移除用户的消息失败！" + e.getMessage(), e);
            }
            if (StringUtils.hasText(messageValue)) {
                messageContent = JSONObject.parseObject(messageValue, WebSocketMsgNoticeFacade.class).getMsg();
            }
        }
        return messageContent;
    }

    private Set<String> getMessagesFromCache(String key) {
        Set<String> userMessageSet = null;
        try {
            userMessageSet = redisService.getSet(key);
        } catch (Exception e) {
            log.error("从redis中获取用户的消息失败！" + e.getMessage(), e);
        }
        return userMessageSet;
    }

}
