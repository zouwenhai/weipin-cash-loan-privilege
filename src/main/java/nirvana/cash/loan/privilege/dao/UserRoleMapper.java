package nirvana.cash.loan.privilege.dao;

import nirvana.cash.loan.privilege.common.config.MyMapper;
import nirvana.cash.loan.privilege.domain.UserRole;

import java.util.List;

public interface UserRoleMapper extends MyMapper<UserRole> {

    List<String> findRoleCodeListByUserId(Integer userId);

    List<Long> findUserIdListByRoleId(Long roleId);
}