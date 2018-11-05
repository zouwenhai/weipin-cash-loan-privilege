package nirvana.cash.loan.privilege.service;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018/8/29.
 */
public interface LogoutUserService {

    //查询所有登录用户jsessionid
    Set<String> findAllLoginJsessionid();

    //查询指定用户jsessionid
    String findJsessionidByUserId(Long userId,Set<String> jsessionids);

    //退出指定登录用户
    void logoutUser(Long userId);

    //批量退出登录用户
    void batchLogoutUser(List<Long> userIdList);
}
