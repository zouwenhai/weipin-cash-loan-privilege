package nirvana.cash.loan.privilege.domain.vo;

import lombok.Data;

/**
 * 消息配置详情
 * Created by Administrator on 2018/11/7.
 */
@Data
public class MsgConfigDetailVo {

    //消息渠道
    private Integer msgChannel;

    //消息发送对象
    private String msgTarget;

    //消息发送开始时间
    private String startTime;

    //消息发送结束时间
    private String endTime;

}
