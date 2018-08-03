package nirvana.cash.loan.privilege.system.service;

import java.util.List;
import java.util.Map;

import nirvana.cash.loan.privilege.common.domain.Tree;
import nirvana.cash.loan.privilege.common.service.IService;
import nirvana.cash.loan.privilege.system.domain.Menu;
import nirvana.cash.loan.privilege.system.domain.vo.LeftMenuVo;

public interface MenuService extends IService<Menu> {

	List<Menu> findUserPermissions(String userName);

	List<Menu> findUserMenus(String userName);

	List<Menu> findAllMenus(Menu menu);

	Tree<Menu> getMenuButtonTree();
	
	Tree<Menu> getMenuTree();
	
	Tree<Menu> getUserMenu(String userName);
	
	Menu findById(Long menuId);

	Menu findByNameAndType(String menuName, String type);

	void addMenu(Menu menu);

	void updateMenu(Menu menu);
	
	void deleteMeuns(String menuIds);

	List<LeftMenuVo> findUserMenus();
}
