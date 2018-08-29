package nirvana.cash.loan.privilege.system.service.impl;

import nirvana.cash.loan.privilege.common.domain.SplitMenu;
import nirvana.cash.loan.privilege.common.enums.RoleEnum;
import nirvana.cash.loan.privilege.common.service.impl.BaseService;
import nirvana.cash.loan.privilege.system.dao.RoleMapper;
import nirvana.cash.loan.privilege.system.dao.RoleMenuMapper;
import nirvana.cash.loan.privilege.system.domain.Menu;
import nirvana.cash.loan.privilege.system.domain.Role;
import nirvana.cash.loan.privilege.system.domain.RoleMenu;
import nirvana.cash.loan.privilege.system.domain.RoleWithMenu;
import nirvana.cash.loan.privilege.system.service.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

	@Autowired
	private MenuService menuService;

	@Autowired
	private LogoutUserService logoutUserService;

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
			return new ArrayList<>();
		}
	}

	@Override
	public Role findByCode(String roleCode) {
		Example example = new Example(Role.class);
		example.createCriteria().andCondition("lower(role_code)=", roleCode);
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
        role.setModifyTime(new Date());
		role.setRoleName(RoleEnum.getPaymentStatusEnumByValue(role.getRoleCode()).getName());
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
	public void deleteRoles(Long roleId) {
		List<Long> userIdList = userRoleService.findUserIdListByRoleId(roleId);
		logoutUserService.batchLogoutUser(userIdList);

		List<String> list = new ArrayList<>();
		list.add(roleId.toString());
		this.batchDelete(list, "roleId", Role.class);
		this.roleMenuService.deleteRoleMenusByRoleId(roleId.toString());
		this.userRoleService.deleteUserRolesByRoleId(roleId.toString());
	}


	@Override
	public RoleWithMenu findRoleWithMenus(Long roleId) {
		Role role= this.selectByKey(roleId);
		List<Menu> allList = menuService.findAllMenus(new Menu());
		List<RoleWithMenu> roleWithMenuList = this.roleMapper.findById(roleId);

		SplitMenu splitMenu = new SplitMenu();
		splitMenu.splitMenuList(allList,roleWithMenuList);
		List<Long> parentIds=splitMenu.getParentList().stream().map(t->t.getMenuId()).collect(Collectors.toList());
		List<Long> leafIds=splitMenu.getLeafList().stream().map(t->t.getMenuId()).collect(Collectors.toList());

		//其他角色信息
		RoleWithMenu roleWithMenu = new RoleWithMenu();
        roleWithMenu.setRoleCode(role.getRoleCode());
		roleWithMenu.setRoleId(role.getRoleId());
		roleWithMenu.setRoleName(role.getRoleName());
		roleWithMenu.setRemark(role.getRemark());
		roleWithMenu.setMenuIds(parentIds);
		roleWithMenu.setButtonIds(leafIds);
		return roleWithMenu;
	}

	@Override
	@Transactional
	public void updateRole(Role role, Long[] menuIds) {
		role.setModifyTime(new Date());
		role.setRoleName(RoleEnum.getPaymentStatusEnumByValue(role.getRoleCode()).getName());
		this.updateNotNull(role);

		List<Long> userIdList = userRoleService.findUserIdListByRoleId(role.getRoleId());
		logoutUserService.batchLogoutUser(userIdList);

		Example example = new Example(RoleMenu.class);
		example.createCriteria().andCondition("role_id=", role.getRoleId());
		this.roleMenuMapper.deleteByExample(example);
		setRoleMenus(role, menuIds);
	}

}
