package nirvana.cash.loan.privilege.websocket;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author dongdong
 * @date 2018/11/7
 */
public class MessageContainer {

    private static Map<String, List<String>> container = new ConcurrentHashMap<>();

    public static void put(String username, String messageContext) {
        List<String> userMessages = container.get(username);
        if (userMessages == null) {
            synchronized (MessageContainer.class) {
                userMessages = container.get(username);
                if (userMessages == null) {
                    userMessages = new CopyOnWriteArrayList<>();
                    container.put(username, userMessages);
                }
            }
        }
        userMessages.add(messageContext);
    }

    public static String getOneMessageByUsername(String username) {
        String message = null;
        List<String> userMessages = container.get(username);
        if (!CollectionUtils.isEmpty(userMessages)) {
            message = userMessages.remove(0);
        }
        return message;
    }

}
