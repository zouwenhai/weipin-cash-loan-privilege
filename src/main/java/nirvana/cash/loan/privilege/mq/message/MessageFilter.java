package nirvana.cash.loan.privilege.mq.message;

import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.domain.UserWithRole;
import nirvana.cash.loan.privilege.mq.facade.MessageFacade;
import nirvana.cash.loan.privilege.service.DeptProductService;
import nirvana.cash.loan.privilege.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @author dongdong
 * @date 2018/12/18
 */
@Slf4j
@Component
public class MessageFilter {
    
    @Autowired
    private UserService userService;
    @Autowired
    private DeptProductService deptProductService;

    /**
     * 判断用户是否有权限接收该消息
     *
     * @param userId
     * @param messageFacade
     * @return
     */
    public boolean hasPrivilegeToReceive(Long userId, MessageFacade messageFacade) {
        Long productId = messageFacade.getProductNo();
        if (productId == null) {
            return false;
        }
        //获取用户所有的部门，得到部门下的管理的产品的集合
        UserWithRole user = userService.findById(userId);
        if (user == null) {
            return false;
        }
        String deptIdStr = user.getDeptId();
        if (StringUtils.hasText(deptIdStr)) {
            String regex = ",";
            String[] deptIds = deptIdStr.split(regex);
            for (String deptId : deptIds) {
                String productNos = deptProductService.findProductNosByDeptId(Long.valueOf(deptId));
                if (!StringUtils.hasText(productNos)) {
                    continue;
                }
                String productIdStr = String.valueOf(productId);
                for (String productNo : productNos.split(regex)) {
                    if (Objects.equals(productNo, productIdStr)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
