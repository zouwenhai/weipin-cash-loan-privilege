package nirvana.cash.loan.privilege.service.impl;

import java.util.Arrays;
import java.util.List;

import nirvana.cash.loan.privilege.dao.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import nirvana.cash.loan.privilege.service.base.impl.BaseService;
import nirvana.cash.loan.privilege.domain.UserRole;
import nirvana.cash.loan.privilege.service.UserRoleService;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserRoleServiceImpl extends BaseService<UserRole> implements UserRoleService {

	@Autowired
	private UserRoleMapper userRoleMapper;

	@Override
	@Transactional
	public void deleteUserRolesByRoleId(String roleIds) {
		List<String> list = Arrays.asList(roleIds.split(","));
		this.batchDelete(list, "roleId", UserRole.class);
	}

	@Override
	@Transactional
	public void deleteUserRolesByUserId(String userIds) {
		List<String> list = Arrays.asList(userIds.split(","));
		this.batchDelete(list, "userId", UserRole.class);
	}

	@Override
	public List<String> findRoleCodeListByUserId(Integer userId) {
		return userRoleMapper.findRoleCodeListByUserId(userId);
	}

    @Override
    public List<Long> findUserIdListByRoleId(Long roleId) {
        return userRoleMapper.findUserIdListByRoleId(roleId);
    }

}
