package nirvana.cash.loan.privilege.service;

import nirvana.cash.loan.privilege.common.domain.Tree;
import nirvana.cash.loan.privilege.domain.Dept;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.vo.AuthDeptProductInfoVo;
import nirvana.cash.loan.privilege.service.base.IService;

import java.util.List;

public interface DeptService extends IService<Dept> {

	Tree<Dept> getDeptTree();

	List<Dept> findAllDepts(Dept dept);

	Dept findByName(String deptName);

	Dept findById(Long deptId);
	
	void addDept(Dept dept);
	
	void updateDept(Dept dept, User loginUser);

	AuthDeptProductInfoVo findAuthDeptProductInfoFromCache(Long userId,Long deptId);
}
