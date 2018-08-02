package nirvana.cash.loan.privilege.system.dao;

import java.util.List;

import nirvana.cash.loan.privilege.common.config.MyMapper;
import nirvana.cash.loan.privilege.system.domain.User;
import nirvana.cash.loan.privilege.system.domain.UserWithRole;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserMapper extends MyMapper<User> {

	List<User> findUserWithDept(User user);
	
	List<UserWithRole> findUserWithRole(Long userId);
	
	User findUserProfile(User user);

    String findUserRoldIds(@RequestParam("userId") Integer userId);
}