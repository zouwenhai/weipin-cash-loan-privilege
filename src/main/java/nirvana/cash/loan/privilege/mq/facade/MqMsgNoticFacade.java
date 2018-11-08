package nirvana.cash.loan.privilege.mq.facade;

import lombok.Data;

/**
 * 消息中心-推送的消息格式
 * Created by Administrator on 2018/11/7.
 */
@Data
public class MqMsgNoticFacade {


    /**
     * UUID
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
     * 订单备注
     */
    private String orderRemark;


}
