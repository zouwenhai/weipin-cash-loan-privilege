package nirvana.cash.loan.privilege.system.service;

import nirvana.cash.loan.privilege.common.service.IService;
import nirvana.cash.loan.privilege.system.domain.RoleMenu;

import java.util.List;

public interface RoleMenuServie extends IService<RoleMenu> {

	void deleteRoleMenusByRoleId(String roleIds);

	void deleteRoleMenusByMenuId(String menuIds);

	void deleteRoleMenusByMenuId(List menuIds);
}
