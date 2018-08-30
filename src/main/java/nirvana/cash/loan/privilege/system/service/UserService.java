package nirvana.cash.loan.privilege.system.service;

import nirvana.cash.loan.privilege.common.service.IService;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.system.domain.User;
import nirvana.cash.loan.privilege.system.domain.UserWithRole;

import java.util.List;

public interface UserService extends IService<User> {

	UserWithRole findById(Long userId);
	
	User findByName(String userName);

	List<User> findUserWithDept(User user);

	ResResult addUser(User user, Long[] roles);

	void updateUser(User user, Long[] roles,Long loginUserId);
	
	void deleteUser(Integer userId);

	void updateLoginTime(String userName);
	
	void updatePassword(String password,Long userId);

	String findUserRoldIds(Integer userId);
}
