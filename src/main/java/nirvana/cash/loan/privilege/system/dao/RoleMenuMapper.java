package nirvana.cash.loan.privilege.system.dao;

import nirvana.cash.loan.privilege.common.config.MyMapper;
import nirvana.cash.loan.privilege.system.domain.RoleMenu;

import java.util.List;

public interface RoleMenuMapper extends MyMapper<RoleMenu> {

    List<Long> findUserIdListByMenuId(Long menuId);
}