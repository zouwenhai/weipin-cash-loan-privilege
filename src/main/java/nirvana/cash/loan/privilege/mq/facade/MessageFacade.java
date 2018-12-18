package nirvana.cash.loan.privilege.mq.facade;

import lombok.Data;

import java.util.List;

/**
 * @author dongdong
 * @date 2018/11/6
 */
@Data
public class MessageFacade {

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
     * 消息通知模块
     */
    private Integer messageModule;

    /**
     * 要接收消息的人
     */
    private List<Long> userIds;

    /**
     * 要接收消息的人的登录名
     */
    private List<String> loginNames;

    /**
     * 消息详细描述
     */
    private String details;

    /**
     * 产品id
     */
    private Long productId;

}
