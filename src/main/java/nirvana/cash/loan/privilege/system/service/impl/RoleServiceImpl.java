package nirvana.cash.loan.privilege.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nirvana.cash.loan.privilege.system.dao.RoleMapper;
import nirvana.cash.loan.privilege.system.dao.RoleMenuMapper;
import nirvana.cash.loan.privilege.system.domain.Role;
import nirvana.cash.loan.privilege.system.domain.RoleMenu;
import nirvana.cash.loan.privilege.system.service.RoleMenuServie;
import nirvana.cash.loan.privilege.system.service.RoleService;
import nirvana.cash.loan.privilege.system.service.UserRoleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import nirvana.cash.loan.privilege.common.service.impl.BaseService;
import nirvana.cash.loan.privilege.system.domain.RoleWithMenu;
import tk.mybatis.mapper.entity.Example;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class RoleServiceImpl extends BaseService<Role> implements RoleService {

	@Autowired
	private RoleMapper roleMapper;

	@Autowired
	private RoleMenuMapper roleMenuMapper;

	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private RoleMenuServie roleMenuService;

	@Override
	public List<Role> findUserRole(String userName) {
		return this.roleMapper.findUserRole(userName);
	}

	@Override
	public List<Role> findAllRole(Role role) {
		try {
			Example example = new Example(Role.class);
			if (StringUtils.isNotBlank(role.getRoleName())) {
				example.createCriteria().andCondition("role_name=", role.getRoleName());
			}
			example.setOrderByClause("create_time");
			return this.selectByExample(example);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	@Override
	public Role findByName(String roleName) {
		Example example = new Example(Role.class);
		example.createCriteria().andCondition("lower(role_name)=", roleName.toLowerCase());
		List<Role> list = this.selectByExample(example);
		if (list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	@Transactional
	public void addRole(Role role, Long[] menuIds) {
		role.setRoleId(this.getSequence(Role.SEQ));
		role.setCreateTime(new Date());
		this.save(role);
		setRoleMenus(role, menuIds);
	}

	private void setRoleMenus(Role role, Long[] menuIds) {
		for (Long menuId : menuIds) {
			RoleMenu rm = new RoleMenu();
			rm.setMenuId(menuId);
			rm.setRoleId(role.getRoleId());
			this.roleMenuMapper.insert(rm);
		}
	}

	@Override
	@Transactional
	public void deleteRoles(String roleIds) {
		List<String> list = Arrays.asList(roleIds.split(","));
		this.batchDelete(list, "roleId", Role.class);

		this.roleMenuService.deleteRoleMenusByRoleId(roleIds);
		this.userRoleService.deleteUserRolesByRoleId(roleIds);

	}

	@Override
	public RoleWithMenu findRoleWithMenus(Long roleId) {
		List<RoleWithMenu> list = this.roleMapper.findById(roleId);
		List<Long> menuList = new ArrayList<>();
		for (RoleWithMenu rwm : list) {
			menuList.add(rwm.getMenuId());
		}
		if (list.size() == 0) {
			return null;
		}
		RoleWithMenu roleWithMenu = list.get(0);
		roleWithMenu.setMenuIds(menuList);
		return roleWithMenu;
	}

	@Override
	@Transactional
	public void updateRole(Role role, Long[] menuIds) {
		role.setModifyTime(new Date());
		this.updateNotNull(role);
		Example example = new Example(RoleMenu.class);
		example.createCriteria().andCondition("role_id=", role.getRoleId());
		this.roleMenuMapper.deleteByExample(example);
		setRoleMenus(role, menuIds);
	}

}
