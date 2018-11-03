package nirvana.cash.loan.privilege.service;

import nirvana.cash.loan.privilege.service.base.IService;
import nirvana.cash.loan.privilege.domain.RoleMenu;

import java.util.List;

public interface RoleMenuServie extends IService<RoleMenu> {

	void deleteRoleMenusByRoleId(String roleIds);

	void deleteRoleMenusByMenuId(List menuIds);

	List<Long> findUserIdListByMenuId(Long menuId);
}
