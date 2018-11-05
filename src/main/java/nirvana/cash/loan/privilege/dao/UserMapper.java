package nirvana.cash.loan.privilege.dao;

import java.util.List;

import nirvana.cash.loan.privilege.common.config.MyMapper;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.UserWithRole;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends MyMapper<User> {

	List<User> findUserWithDept(User user);
	
	List<UserWithRole> findUserWithRole(Long userId);

    String findUserRoldIds(@Param("userId") Integer userId);

    void setDeptIdNull(Long deptId);
}