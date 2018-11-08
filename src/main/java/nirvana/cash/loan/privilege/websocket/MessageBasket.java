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

import java.util.HashSet;
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

    /**
     *
     * @param userId
     * @return
     */
    public Set<String> fetchUserMessages(String userId) {
        String key = RedisKeyContant.YOFISHDK_MSG_NOTICE_PREFIX + userId;
        Set<String> messagesFromCache = getMessagesFromCache(key), messageContents = new HashSet<>();
        if (!CollectionUtils.isEmpty(messagesFromCache)) {
            try {
                redisService.delete(key);
            } catch (Exception e) {
                log.error("从redis中删除用户消息缓存失败！" + e.getMessage(), e);
            }
            messagesFromCache.forEach(m -> {
                if (StringUtils.hasText(m)) {
                    messageContents.add(JSONObject.parseObject(m, WebSocketMsgNoticeFacade.class).getMsg());
                }
            });
        }
        return messageContents;
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
