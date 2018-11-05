package nirvana.cash.loan.privilege.service.impl;

import nirvana.cash.loan.privilege.service.base.impl.BaseService;
import nirvana.cash.loan.privilege.service.RoleMenuServie;
import nirvana.cash.loan.privilege.dao.RoleMenuMapper;
import nirvana.cash.loan.privilege.domain.RoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class RoleMenuServiceImpl extends BaseService<RoleMenu> implements RoleMenuServie {

	@Autowired
	private RoleMenuMapper roleMenuMapper;

	@Override
	@Transactional
	public void deleteRoleMenusByRoleId(String roleIds) {
		List<String> list = Arrays.asList(roleIds.split(","));
		this.batchDelete(list, "roleId", RoleMenu.class);
	}

	@Override
	@Transactional
	public void deleteRoleMenusByMenuId(List menuIds) {
		this.batchDelete(menuIds, "menuId", RoleMenu.class);
	}


	@Override
	public List<Long> findUserIdListByMenuId(Long menuId) {
		return roleMenuMapper.findUserIdListByMenuId(menuId);
	}

}
