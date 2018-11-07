package nirvana.cash.loan.privilege.mq.facade;

import lombok.Data;

/**
 * 消息中心-推送的消息格式
 * Created by Administrator on 2018/11/7.
 */
@Data
public class MqMsgNoticFacade {

    //消息唯一ID
    private String uuid;

    //登录名
    private String loginName;

    //通知模块
    private Integer msgModule;

    //消息内容
    private String content;


}
