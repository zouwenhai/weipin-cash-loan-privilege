package nirvana.cash.loan.privilege.dao;

import java.util.List;

import nirvana.cash.loan.privilege.common.config.MyMapper;
import nirvana.cash.loan.privilege.domain.Role;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.UserWithRole;
import nirvana.cash.loan.privilege.fegin.facade.IsDivideOrderFacade;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends MyMapper<User> {

    List<User> findUserWithDept(User user);

    List<UserWithRole> findUserWithRole(Long userId);

    String findUserRoldIds(@Param("userId") Integer userId);

    void setDeptIdNull(Long deptId);

    List<User> getUserById(@Param("userIdList") List<Long> userIdList, @Param("isSeperate") Integer isSeperate);

    int updateDivideOrder(IsDivideOrderFacade isDivideOrderFacade);
}