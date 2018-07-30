package nirvana.cash.loan.privilege.system.dao;

import java.util.List;

import nirvana.cash.loan.privilege.common.config.MyMapper;
import nirvana.cash.loan.privilege.system.domain.Menu;

public interface MenuMapper extends MyMapper<Menu> {
	
	List<Menu> findUserPermissions(String userName);
	
	List<Menu> findUserMenus(String userName);
	
	// 删除父节点，子节点变成顶级节点（根据实际业务调整）
	void changeToTop(List<String> menuIds);
}