package nirvana.cash.loan.privilege.service;

import nirvana.cash.loan.privilege.service.base.IService;
import nirvana.cash.loan.privilege.domain.UserRole;

import java.util.List;

public interface UserRoleService extends IService<UserRole> {

	void deleteUserRolesByRoleId(String roleIds);

	void deleteUserRolesByUserId(String userIds);

    List<String> findRoleCodeListByUserId(Integer userId);

	List<Long> findUserIdListByRoleId(Long roleId);
}
