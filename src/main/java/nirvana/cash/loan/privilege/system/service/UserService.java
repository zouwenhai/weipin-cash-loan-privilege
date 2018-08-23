package nirvana.cash.loan.privilege.system.service;

import java.util.List;

import nirvana.cash.loan.privilege.common.service.IService;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.system.domain.User;
import nirvana.cash.loan.privilege.system.domain.UserWithRole;

public interface UserService extends IService<User> {

	UserWithRole findById(Long userId);
	
	User findByName(String userName);

	List<User> findUserWithDept(User user);

	void registUser(User user);

	void updateTheme(String theme, String userName);

	ResResult addUser(User user, Long[] roles);

	void updateUser(User user, Long[] roles);
	
	void deleteUser(Integer userId);

	void updateLoginTime(String userName);
	
	void updatePassword(String password,Long userId);
	
	User findUserProfile(User user);
	
	void updateUserProfile(User user);

	String findUserRoldIds(Integer userId);
}
