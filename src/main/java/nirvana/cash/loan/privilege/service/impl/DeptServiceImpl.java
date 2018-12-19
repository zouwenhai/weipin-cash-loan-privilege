package nirvana.cash.loan.privilege.service.impl;

import nirvana.cash.loan.privilege.common.contants.RedisKeyContant;
import nirvana.cash.loan.privilege.common.domain.FilterId;
import nirvana.cash.loan.privilege.common.domain.Tree;
import nirvana.cash.loan.privilege.common.util.TreeUtils;
import nirvana.cash.loan.privilege.dao.DeptMapper;
import nirvana.cash.loan.privilege.dao.UserMapper;
import nirvana.cash.loan.privilege.domain.Dept;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.service.DeptProductService;
import nirvana.cash.loan.privilege.service.DeptService;
import nirvana.cash.loan.privilege.service.LogoutUserService;
import nirvana.cash.loan.privilege.service.base.RedisService;
import nirvana.cash.loan.privilege.service.base.impl.BaseService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class DeptServiceImpl extends BaseService<Dept> implements DeptService {

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private DeptProductService deptProductService;
	@Autowired
	private RedisService redisService;
	@Autowired
	private LogoutUserService logoutUserService;
	@Autowired
	private DeptMapper deptMapper;

	@Override
	public Tree<Dept> getDeptTree() {
		List<Tree<Dept>> trees = new ArrayList<>();
		List<Dept> depts = this.findAllDepts(new Dept());
		for (Dept dept : depts) {
			Tree<Dept> tree = new Tree<>();
			tree.setId(dept.getDeptId().toString());
			tree.setParentId(dept.getParentId().toString());
			tree.setText(dept.getDeptName());
			trees.add(tree);
		}
		return TreeUtils.build(trees);
	}

	@Override
	public List<Dept> findAllDepts(Dept dept) {
		Example example = new Example(Dept.class);
		Example.Criteria criteria= example.createCriteria()
				.andEqualTo("isDelete",0);
		if(StringUtils.isNotBlank(dept.getDeptName())){
			criteria.andEqualTo("deptName",dept.getDeptName());
		}
		example.setOrderByClause("dept_id");
		return  this.selectByExample(example);
	}

	@Override
	public Dept findByName(String deptName) {
		Example example = new Example(Dept.class);
		example.createCriteria()
				.andEqualTo("isDelete",0)
				.andEqualTo("deptName",deptName);
		List<Dept> list = this.selectByExample(example);
		if (list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	@Transactional
	public void addDept(Dept dept) {
		Long parentId = dept.getParentId();
		if (parentId == null)
			dept.setParentId(0L);
		dept.setDeptId(this.getSequence(Dept.SEQ));
		dept.setCreateTime(new Date());
		dept.setIsDelete(0);
		this.save(dept);

		//添加部门产品关联信息
		Long deptId = dept.getDeptId();
		String productNos = dept.getProductNos();
		deptProductService.insert(deptId,productNos);
	}

	@Override
	@Transactional
	public void deleteDepts(Long deptId, User loginUser) {
		List<Dept> depts = this.findAllDepts(new Dept());
		//转换列表
		List<FilterId> allList = new ArrayList<>();
		depts.forEach(t -> {
			FilterId filterId = new FilterId(t.getDeptId(), t.getParentId(), t.getDeptName());
			allList.add(filterId);
		});
		//开始处理...
		List<FilterId> filterIdList = FilterId.filterRemoveList(allList, deptId);
		List<String> list =new ArrayList<>();
		for(FilterId item:filterIdList){
			list.add(item.getId()+"");
		}
		//更新为删除状态
		Example exampleDept = new Example(Dept.class);
		exampleDept.createCriteria().andIn("deptId",list);
		Dept updateDept = new Dept();
		updateDept.setIsDelete(1);
		deptMapper.updateByExampleSelective(updateDept,exampleDept);

		//删除关联产品缓存
		String redisKey = RedisKeyContant.yofishdk_auth_productnos_prefix + deptId;
		redisService.delete(redisKey);

		//关联登录用户强制退出
		Example exampleUser = new Example(User.class);
		exampleUser.createCriteria().andIn("deptId",list);
		List<User> userList =  userMapper.selectByExample(exampleUser);
		List<Long> userIds = userList.stream().filter(t->t.getDeptId()!=null).filter(t->!t.getDeptId().equals(loginUser.getDeptId())).map(t->t.getUserId()).collect(Collectors.toList());
		logoutUserService.batchLogoutUser(userIds);

		//部门ID，重置为null
		userMapper.setDeptIdNull(deptId);
	}

	@Override
	public Dept findById(Long deptId) {
		return this.selectByKey(deptId);
	}

	@Override
	@Transactional
	public void updateDept(Dept dept, User loginUser) {
		this.updateNotNull(dept);

		//重新添加部门产品关联信息
		Long deptId = dept.getDeptId();
		String productNos = dept.getProductNos();
		deptProductService.delete(deptId);
		deptProductService.insert(deptId,productNos);

		//删除关联产品缓存
		String redisKey = RedisKeyContant.yofishdk_auth_productnos_prefix + deptId;
		redisService.delete(redisKey);

		//关联登录用户强制退出
		Example example = new Example(User.class);
		example.createCriteria().andEqualTo("deptId",deptId);
		List<User> userList =  userMapper.selectByExample(example);
		List<Long> userIds = userList.stream().filter(t->t.getDeptId()!=null).filter(t->!t.getDeptId().equals(loginUser.getDeptId())).map(t->t.getUserId()).collect(Collectors.toList());
		logoutUserService.batchLogoutUser(userIds);
	}

}
