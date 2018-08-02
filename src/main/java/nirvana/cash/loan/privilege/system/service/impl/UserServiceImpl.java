package nirvana.cash.loan.privilege.system.service.impl;

import nirvana.cash.loan.privilege.common.service.impl.BaseService;
import nirvana.cash.loan.privilege.common.util.MD5Utils;
import nirvana.cash.loan.privilege.system.dao.UserMapper;
import nirvana.cash.loan.privilege.system.dao.UserRoleMapper;
import nirvana.cash.loan.privilege.system.domain.User;
import nirvana.cash.loan.privilege.system.domain.UserRole;
import nirvana.cash.loan.privilege.system.domain.UserWithRole;
import nirvana.cash.loan.privilege.system.service.UserRoleService;
import nirvana.cash.loan.privilege.system.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl extends BaseService<User> implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserRoleMapper userRoleMapper;

	@Autowired
	private UserRoleService userRoleService;

	@Override
	public User findByName(String userName) {
		Example example = new Example(User.class);
		example.createCriteria().andCondition("lower(username)=", userName.toLowerCase());
		List<User> list = this.selectByExample(example);
		if (list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public List<User> findUserWithDept(User user) {
		try {
			if(StringUtils.isNotBlank(user.getUsername())){
				user.setUsername(user.getUsername().toLowerCase());
			}
			return this.userMapper.findUserWithDept(user);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	@Override
	@Transactional
	public void registUser(User user) {
		user.setUserId(this.getSequence(User.SEQ));
		user.setCrateTime(new Date());
		user.setTheme(User.DEFAULT_THEME);
		user.setAvatar(User.DEFAULT_AVATAR);
		user.setSsex(User.SEX_UNKNOW);
		user.setPassword(MD5Utils.encrypt(user.getUsername(), user.getPassword()));
		this.save(user);
		UserRole ur = new UserRole();
		ur.setUserId(user.getUserId());
		ur.setRoleId(3L);
		this.userRoleMapper.insert(ur);
	}

	@Override
	@Transactional
	public void updateTheme(String theme, String userName) {
		Example example = new Example(User.class);
		example.createCriteria().andCondition("username=", userName);
		User user = new User();
		user.setTheme(theme);
		this.userMapper.updateByExampleSelective(user, example);
	}

	@Override
	@Transactional
	public void addUser(User user, Long[] roles) {
		user.setUserId(this.getSequence(User.SEQ));
		user.setCrateTime(new Date());
		user.setTheme(User.DEFAULT_THEME);
		user.setAvatar(User.DEFAULT_AVATAR);
		user.setPassword(MD5Utils.encrypt(user.getUsername(), user.getPassword()));
		this.save(user);
		setUserRoles(user, roles);
	}

	private void setUserRoles(User user, Long[] roles) {
		for (Long roleId : roles) {
			UserRole ur = new UserRole();
			ur.setUserId(user.getUserId());
			ur.setRoleId(roleId);
			this.userRoleMapper.insert(ur);
		}
	}

	@Override
	@Transactional
	public void updateUser(User user, Long[] roles) {
		user.setPassword(null);
		user.setUsername(null);
		user.setModifyTime(new Date());
		this.updateNotNull(user);
		Example example = new Example(UserRole.class);
		example.createCriteria().andCondition("user_id=", user.getUserId());
		this.userRoleMapper.deleteByExample(example);
		setUserRoles(user, roles);
	}

	@Override
	@Transactional
	public void deleteUser(Integer userId) {
		List<String> list = Arrays.asList(userId.toString().split(","));
		User user = userMapper.selectByPrimaryKey(Long.valueOf(userId));
		//system账号禁止删除
		if(user!=null && !"system".equals(user.getUsername().trim())){
			this.batchDelete(list, "userId", User.class);
			this.userRoleService.deleteUserRolesByUserId(userId.toString());
		}
	}

	@Override
	@Transactional
	public void updateLoginTime(String userName) {
		Example example = new Example(User.class);
		example.createCriteria().andCondition("lower(username)=", userName.toLowerCase());
		User user = new User();
		user.setLastLoginTime(new Date());
		this.userMapper.updateByExampleSelective(user, example);
	}

	@Override
	@Transactional
	public void updatePassword(String password,User user) {
		Example example = new Example(User.class);
		example.createCriteria().andCondition("username=", user.getUsername());
		String newPassword = MD5Utils.encrypt(user.getUsername().toLowerCase(), password);
		user.setPassword(newPassword);
		this.userMapper.updateByExampleSelective(user, example);
	}

	@Override
	public UserWithRole findById(Long userId) {
		List<UserWithRole> list = this.userMapper.findUserWithRole(userId);
		List<Long> roleList = new ArrayList<>();
		for (UserWithRole uwr : list) {
			roleList.add(uwr.getRoleId());
		}
		if (list.size() == 0) {
			return null;
		}
		UserWithRole userWithRole = list.get(0);
		userWithRole.setRoleIds(roleList);
		return userWithRole;
	}

	@Override
	public User findUserProfile(User user) {
		return this.userMapper.findUserProfile(user);
	}

	@Override
	@Transactional
	public void updateUserProfile(User user) {
		user.setUsername(null);
		user.setPassword(null);
		if (user.getDeptId() == null)
			user.setDeptId(0L);
		this.updateNotNull(user);
	}

	@Override
	public String findUserRoldIds(Integer userId) {
		return this.userMapper.findUserRoldIds(userId);
	}

}
