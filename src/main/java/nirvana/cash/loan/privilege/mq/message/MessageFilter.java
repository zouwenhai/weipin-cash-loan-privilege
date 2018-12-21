package nirvana.cash.loan.privilege.mq.message;

import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.contants.CommonContants;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.mq.facade.MessageFacade;
import nirvana.cash.loan.privilege.service.UserService;
import nirvana.cash.loan.privilege.web.RequestCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
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
    private RequestCheck requestCheck;

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
        User user = userService.selectByKey(userId);
        if (user == null) {
            return false;
        }
        Map<String, String> deptAndProductAuth = requestCheck.findDeptAndProductAuth(user);
        String authShowIds = deptAndProductAuth.get("authShowIds");
        System.out.println(authShowIds);
        //拥有任何产品的权限
        if (Objects.equals(CommonContants.default_all_product_no, authShowIds)) {
            return true;
        }

        //没有任何产品的权限
        if (Objects.equals(CommonContants.default_product_no, authShowIds)) {
            return false;
        }

        //拥有部分产品的权限
        if (StringUtils.hasText(authShowIds)) {
            String regex = ",";
            String[] showIds = authShowIds.split(regex);
            for (String showId : showIds) {
                if (Objects.equals(showId, String.valueOf(productId))) {
                    return true;
                }
            }
        }
        return false;
    }

}
