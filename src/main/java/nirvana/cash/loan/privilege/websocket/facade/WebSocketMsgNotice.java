package nirvana.cash.loan.privilege.websocket.facade;

import lombok.Data;

/**
 * Created by Administrator on 2018/11/7.
 */
@Data
public class WebSocketMsgNotice {

    private String uuid;

    private String userId;

    private String msg;
}
