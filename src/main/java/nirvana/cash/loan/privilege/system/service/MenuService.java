package nirvana.cash.loan.privilege.system.service;

import nirvana.cash.loan.privilege.common.domain.Tree;
import nirvana.cash.loan.privilege.common.service.IService;
import nirvana.cash.loan.privilege.system.domain.Menu;
import nirvana.cash.loan.privilege.system.domain.vo.LeftMenuVo;

import java.util.List;

public interface MenuService extends IService<Menu> {

	List<Menu> findUserPermissions(String userName);

	List<Menu> findAllMenus(Menu menu);

	Tree<Menu> getMenuButtonTree();
	
	Tree<Menu> getMenuTree();

	Menu findById(Long menuId);

	void addMenu(Menu menu);

	void updateMenu(Menu menu,Long loginUserId);
	
	void deleteMeuns(Long menuIds,Long loginUserId);

	List<LeftMenuVo> findUserMenus();
}
