package nirvana.cash.loan.privilege.dao;

import java.util.List;

import nirvana.cash.loan.privilege.common.config.MyMapper;
import nirvana.cash.loan.privilege.domain.Role;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.UserWithRole;
import nirvana.cash.loan.privilege.fegin.facade.ExtNumberFacade;
import nirvana.cash.loan.privilege.fegin.facade.IsDivideOrderFacade;
import nirvana.cash.loan.privilege.fegin.facade.IsOpenSeatFacade;
import nirvana.cash.loan.privilege.fegin.facade.OrderTopFacade;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends MyMapper<User> {

    List<User> findUserWithDept(User user);

    List<UserWithRole> findUserWithRole(Long userId);

    String findUserRoldIds(@Param("userId") Integer userId);

    void setDeptIdNull(Long deptId);

    List<User> getUserById(@Param("userIdList") List<Long> userIdList, @Param("isSeperate") Integer isSeperate);

    int updateDivideOrder(IsDivideOrderFacade isDivideOrderFacade);

    /**
     * 获取借款订单审核专员
     *
     * @param isSeperate
     * @return
     */
    List<User> getAuditUser(@Param("isSeperate") Integer isSeperate);

    /**
     * 修改坐席状态
     *
     * @param isOpenSeatFacade
     */
    void updateSeat(IsOpenSeatFacade isOpenSeatFacade);

    /**
     * 修改分机号
     *
     * @param extNumberFacade
     */
    void updateExtNumber(ExtNumberFacade extNumberFacade);

    /**
     * 获取复审专员信息
     *
     * @param isSeperate
     * @return
     */
    List<User> getReviewUser(@Param("isSeperate") Integer isSeperate);

    /**
     * 修改接单上限
     *
     * @param orderTopFacade
     */

    void updateOrderTop(OrderTopFacade orderTopFacade);
}