package nirvana.cash.loan.privilege.service;

import nirvana.cash.loan.privilege.common.domain.Tree;
import nirvana.cash.loan.privilege.service.base.IService;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.domain.Menu;
import nirvana.cash.loan.privilege.domain.vo.LeftMenuVo;

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

	//按orderNum字段升序排序
	List<Menu> findByParentId(Long parentId);

	void batchUpdateByIds(List<Menu> menuList);

	void resetOrderNum(Long parentId);

	ResResult menuSort(Long menuId, Long orderNum);
}
