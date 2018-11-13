package nirvana.cash.loan.privilege.websocket.facade;

import lombok.Data;

/**
 * Created by Administrator on 2018/11/7.
 */
@Data
public class WebSocketMessageFacade {
    //消息唯一ID
    private String uuid;
    //消息接收方ID
    private Long userId;
    //消息内容
    private String msg;
    //未读消息数量
    private Integer count;
}
