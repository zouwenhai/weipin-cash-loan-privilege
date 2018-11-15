package nirvana.cash.loan.privilege.mq.facade;

import lombok.Data;

/**
 * @author dongdong
 * @date 2018/11/6
 */
@Data
public class OrderStatusChangeFacade {

    /**
     * 消息唯一ID
     */
    private String uuid;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 消息接收者
     */
    private String orderUser;
}
