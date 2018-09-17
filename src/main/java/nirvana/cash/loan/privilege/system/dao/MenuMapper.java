package nirvana.cash.loan.privilege.system.dao;

import java.util.List;
import java.util.Map;

import nirvana.cash.loan.privilege.common.config.MyMapper;
import nirvana.cash.loan.privilege.system.domain.Menu;
import nirvana.cash.loan.privilege.system.domain.vo.LeftMenuVo;

public interface MenuMapper extends MyMapper<Menu> {

	//查询权限列表
	List<Menu> findUserPermissions(String userName);

	//左侧菜单列表
	List<LeftMenuVo> findLeftMenuList();
}