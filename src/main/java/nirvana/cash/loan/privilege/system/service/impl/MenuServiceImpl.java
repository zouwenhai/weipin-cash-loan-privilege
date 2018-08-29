package nirvana.cash.loan.privilege.system.service.impl;

import nirvana.cash.loan.privilege.common.domain.FilterId;
import nirvana.cash.loan.privilege.common.domain.Tree;
import nirvana.cash.loan.privilege.common.service.impl.BaseService;
import nirvana.cash.loan.privilege.common.util.TreeUtils;
import nirvana.cash.loan.privilege.system.dao.MenuMapper;
import nirvana.cash.loan.privilege.system.domain.Menu;
import nirvana.cash.loan.privilege.system.domain.vo.LeftMenuVo;
import nirvana.cash.loan.privilege.system.service.LogoutUserService;
import nirvana.cash.loan.privilege.system.service.MenuService;
import nirvana.cash.loan.privilege.system.service.RoleMenuServie;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MenuServiceImpl extends BaseService<Menu> implements MenuService {

	@Autowired
	private MenuMapper menuMapper;

	@Autowired
	private RoleMenuServie roleMenuService;

	@Autowired
	private LogoutUserService logoutUserService;

	@Override
	public List<Menu> findUserPermissions(String userName) {
		return this.menuMapper.findUserPermissions(userName);
	}

	@Override
	public List<Menu> findAllMenus(Menu menu) {
		try {
			Example example = new Example(Menu.class);
			Criteria criteria = example.createCriteria();
			if (StringUtils.isNotBlank(menu.getMenuName())) {
				criteria.andCondition("menu_name=", menu.getMenuName());
			}
			if (StringUtils.isNotBlank(menu.getType())) {
				criteria.andCondition("type=", Long.valueOf(menu.getType()));
			}
			example.setOrderByClause("menu_id");
			return this.selectByExample(example);
		} catch (NumberFormatException e) {
			return new ArrayList<>();
		}
	}

	@Override
	public Tree<Menu> getMenuButtonTree() {
		List<Tree<Menu>> trees = new ArrayList<>();
		List<Menu> menus = this.findAllMenus(new Menu());
		buildTrees(trees, menus);
		return TreeUtils.build(trees);
	}

	@Override
	public Tree<Menu> getMenuTree() {
		List<Tree<Menu>> trees = new ArrayList<>();
		Example example = new Example(Menu.class);
		example.createCriteria().andCondition("type =", 0);
		example.setOrderByClause("create_time");
		List<Menu> menus = this.selectByExample(example);
		buildTrees(trees, menus);
		return TreeUtils.build(trees);
	}

	private void buildTrees(List<Tree<Menu>> trees, List<Menu> menus) {
		for (Menu menu : menus) {
			Tree<Menu> tree = new Tree<>();
			tree.setId(menu.getMenuId().toString());
			tree.setParentId(menu.getParentId().toString());
			tree.setText(menu.getMenuName());
			tree.setMenuType(menu.getType());
			trees.add(tree);
		}
	}

	@Override
	@Transactional
	public void addMenu(Menu menu) {
		menu.setMenuId(this.getSequence(Menu.SEQ));
		menu.setCreateTime(new Date());
		if (menu.getParentId() == null)
			menu.setParentId(0L);
		this.save(menu);
	}

	@Override
	@Transactional
	public void deleteMeuns(Long menuId) {
		List<Menu> menus = this.findAllMenus(new Menu());
		if (menus != null && menus.size() > 0) {
			List<Long> userIdList = roleMenuService.findUserIdListByMenuId(menuId);
			logoutUserService.batchLogoutUser(userIdList);
			//转换列表
			List<FilterId> allList = new ArrayList<>();
			menus.forEach(t -> {
				FilterId filterId = new FilterId(t.getMenuId(), t.getParentId(), t.getMenuName());
				allList.add(filterId);
			});
			//开始处理...
			List<FilterId> filterIdList = FilterId.filterRemoveList(allList, menuId);
			List<String> list =new ArrayList<>();
			for(FilterId item:filterIdList){
				list.add(item.getId()+"");
			}
			this.batchDelete(list, "menuId", Menu.class);
			this.roleMenuService.deleteRoleMenusByMenuId(list);
		}
	}

	@Override
	public Menu findById(Long menuId) {
		return this.selectByKey(menuId);
	}

	@Override
	@Transactional
	public void updateMenu(Menu menu) {
		List<Long> userIdList = roleMenuService.findUserIdListByMenuId(menu.getMenuId());
		logoutUserService.batchLogoutUser(userIdList);

        Menu oldMenu = this.findById(menu.getMenuId());
        menu.setCreateTime(oldMenu.getCreateTime());
		menu.setModifyTime(new Date());
		if (menu.getParentId() == null){
			menu.setParentId(0L);
		}
        this.updateAll(menu);
	}

	@Override
	public List<LeftMenuVo> findUserMenus() {
		return this.menuMapper.findLeftMenuList();
	}


}
