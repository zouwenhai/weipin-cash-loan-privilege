package nirvana.cash.loan.privilege.system.service;

import nirvana.cash.loan.privilege.common.service.IService;
import nirvana.cash.loan.privilege.system.domain.UserRole;

public interface UserRoleService extends IService<UserRole> {

	void deleteUserRolesByRoleId(String roleIds);

	void deleteUserRolesByUserId(String userIds);
}
