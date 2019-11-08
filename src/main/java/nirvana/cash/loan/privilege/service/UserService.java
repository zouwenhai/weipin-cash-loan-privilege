package nirvana.cash.loan.privilege.service;

import nirvana.cash.loan.privilege.fegin.facade.IsDivideOrderFacade;
import nirvana.cash.loan.privilege.service.base.IService;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.UserWithRole;

import java.util.List;
import java.util.Set;

public interface UserService extends IService<User> {

    UserWithRole findById(Long userId);

    User findByName(String userName);

    List<User> findUserWithDept(User user);

    ResResult addUser(User user, List<Long> roles, User loginUser);

    void updateUser(User user, List<Long> roles, Long loginUserId, String username);

    void deleteUser(Integer userId);

    void updateLoginTime(String userName);

    void updatePassword(String password, Long userId);

    String findUserRoldIds(Integer userId);

    String findUserRoldCodes(String roleIds);

    List<User> findByIds(Set<Long> userIdSet);

    List<User> findAllLikeDeptId(Long deptId);


    List<User> findUserById(List<Long> userIdList, Integer isSeperate);

    User getUserById(Long userId);

    /**
     * 是否分单
     *
     * @param isDivideOrderFacade
     * @return
     */
    int isDivideOrder(IsDivideOrderFacade isDivideOrderFacade);


    /**
     * 获取借款订单审核专员
     *
     * @param isSeperate
     * @return
     */
    List<User> getAuditUser(Integer isSeperate);
}
