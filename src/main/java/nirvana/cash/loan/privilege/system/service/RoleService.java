package nirvana.cash.loan.privilege.system.service;

import nirvana.cash.loan.privilege.common.service.IService;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.system.domain.Role;
import nirvana.cash.loan.privilege.system.domain.RoleWithMenu;

import java.util.List;

public interface RoleService extends IService<Role> {

	List<Role> findAllRole(Role role);
	
	RoleWithMenu findRoleWithMenus(Long roleId);

	void addRole(Role role, Long[] menuIds);
	
	void updateRole(Role role, Long[] menuIds,Long loginUserId);

	ResResult deleteRoles(Long roleId, Long loginUserId);

	Role findByCode(String roleCode);

	Role findByRoleName2(String roleName2);

}
