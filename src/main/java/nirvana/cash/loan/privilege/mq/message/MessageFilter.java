package nirvana.cash.loan.privilege.mq.message;

import nirvana.cash.loan.privilege.mq.facade.MessageFacade;
import nirvana.cash.loan.privilege.service.DeptService;
import nirvana.cash.loan.privilege.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author dongdong
 * @date 2018/12/18
 */
@Component
public class MessageFilter {

    @Autowired
    private DeptService deptService;
    @Autowired
    private UserService userService;

    /**
     * 判断用户是否有权限接收该消息
     *
     * @param userId
     * @param messageFacade
     * @return
     */
    public boolean hasPrivilegeToReceive(Long userId, MessageFacade messageFacade) {
        Long productId = messageFacade.getProductId();
        if (productId == null) {
            return false;
        }
        //获取用户所有的部门，得到部门下的管理的产品的集合
        //TODO
        

        List<Long> productIds = null;
        //return productIds.contains(productId);
        return true;
    }

}
