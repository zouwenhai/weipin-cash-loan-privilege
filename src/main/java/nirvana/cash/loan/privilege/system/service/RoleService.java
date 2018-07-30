package nirvana.cash.loan.privilege.system.service;

import java.util.List;

import nirvana.cash.loan.privilege.common.service.IService;
import nirvana.cash.loan.privilege.system.domain.Role;
import nirvana.cash.loan.privilege.system.domain.RoleWithMenu;

public interface RoleService extends IService<Role> {

	List<Role> findUserRole(String userName);

	List<Role> findAllRole(Role role);
	
	RoleWithMenu findRoleWithMenus(Long roleId);

	Role findByName(String roleName);

	void addRole(Role role, Long[] menuIds);
	
	void updateRole(Role role, Long[] menuIds);

	void deleteRoles(String roleIds);
}
