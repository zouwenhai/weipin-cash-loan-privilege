package nirvana.cash.loan.privilege.mq.message;

import nirvana.cash.loan.privilege.mq.facade.MessageFacade;
import org.springframework.stereotype.Component;

/**
 * @author dongdong
 * @date 2018/12/18
 */
@Component
public class MessageFilter {

    /**
     * 判断用户是否有权限接收该消息
     *
     * @return
     */
    public boolean hasPrivilegeToReceive(Long userId, MessageFacade messageFacade) {
        //TODO 过滤掉没有权限接收消息的用户


        return false;
    }

}
