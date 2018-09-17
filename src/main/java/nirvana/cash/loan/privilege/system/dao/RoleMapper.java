package nirvana.cash.loan.privilege.system.dao;

import nirvana.cash.loan.privilege.common.config.MyMapper;
import nirvana.cash.loan.privilege.system.domain.Role;
import nirvana.cash.loan.privilege.system.domain.RoleWithMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper extends MyMapper<Role> {

	List<RoleWithMenu> findById(Long roleId);

	List<String>  findRoleCodeListByRoleIds(@Param("roleIds") List<Integer> roleIds);
}