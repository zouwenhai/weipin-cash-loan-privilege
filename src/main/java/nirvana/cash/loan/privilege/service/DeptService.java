package nirvana.cash.loan.privilege.service;

import java.util.List;

import nirvana.cash.loan.privilege.common.domain.Tree;
import nirvana.cash.loan.privilege.domain.DeptProduct;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.vo.AuthDeptProductInfoVo;
import nirvana.cash.loan.privilege.service.base.IService;
import nirvana.cash.loan.privilege.domain.Dept;

public interface DeptService extends IService<Dept> {

	Tree<Dept> getDeptTree();

	List<Dept> findAllDepts(Dept dept);

	Dept findByName(String deptName);

	Dept findById(Long deptId);
	
	void addDept(Dept dept);
	
	void updateDept(Dept dept, User loginUser);

	void deleteDepts(Long deptId, User loginUser);

	/**
	 * 从缓存获取运营团队权限信息
	 * @param deptId 部门ID
	 * @return
	 */
	AuthDeptProductInfoVo findAuthDeptProductInfoFromCache(Long userId,Long deptId);
}
