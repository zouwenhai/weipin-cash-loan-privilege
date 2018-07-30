package nirvana.cash.loan.privilege.system.service;

import java.util.List;

import nirvana.cash.loan.privilege.common.domain.Tree;
import nirvana.cash.loan.privilege.common.service.IService;
import nirvana.cash.loan.privilege.system.domain.Dept;

public interface DeptService extends IService<Dept> {

	Tree<Dept> getDeptTree();

	List<Dept> findAllDepts(Dept dept);

	Dept findByName(String deptName);

	Dept findById(Long deptId);
	
	void addDept(Dept dept);
	
	void updateDept(Dept dept);

	void deleteDepts(String deptIds);
}
