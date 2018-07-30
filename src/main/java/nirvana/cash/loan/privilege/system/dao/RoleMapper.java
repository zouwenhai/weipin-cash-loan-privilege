package nirvana.cash.loan.privilege.system.dao;

import java.util.List;

import nirvana.cash.loan.privilege.common.config.MyMapper;
import nirvana.cash.loan.privilege.system.domain.Role;
import nirvana.cash.loan.privilege.system.domain.RoleWithMenu;

public interface RoleMapper extends MyMapper<Role> {
	
	List<Role> findUserRole(String userName);
	
	List<RoleWithMenu> findById(Long roleId);
}