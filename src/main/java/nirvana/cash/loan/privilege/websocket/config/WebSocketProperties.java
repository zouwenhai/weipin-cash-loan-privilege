package nirvana.cash.loan.privilege.websocket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author dongdong
 * @date 2018/11/7
 */
@Data
@Component
@ConfigurationProperties(prefix = "websocket")
public class WebSocketProperties {

    /**
     * webSocket服务端每隔指定秒后查询message推送给客户端
     */
    private long delay = 60L;

}
