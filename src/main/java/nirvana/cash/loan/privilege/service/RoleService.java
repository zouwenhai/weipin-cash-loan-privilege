package nirvana.cash.loan.privilege.service;

import nirvana.cash.loan.privilege.service.base.IService;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.domain.Role;
import nirvana.cash.loan.privilege.domain.RoleWithMenu;

import java.util.List;

public interface RoleService extends IService<Role> {

    List<Role> findAllRole(Role role);

    RoleWithMenu findRoleWithMenus(Long roleId);

    void addRole(Role role, List<Long> menuIds);

    void updateRole(Role role, List<Long> menuIds, Long loginUserId);

    ResResult deleteRoles(Long roleId, Long loginUserId);

    Role findByRoleName2(String roleName2);

    List<Role> findRoleByRoleCode(List<String> roleCode);
}
