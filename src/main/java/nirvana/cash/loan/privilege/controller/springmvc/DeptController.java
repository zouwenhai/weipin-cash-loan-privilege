package nirvana.cash.loan.privilege.controller.springmvc;

import nirvana.cash.loan.privilege.common.domain.Tree;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.controller.springmvc.base.BaseController;
import nirvana.cash.loan.privilege.domain.Dept;
import nirvana.cash.loan.privilege.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/privilige")
public class DeptController extends BaseController {

	@Autowired
	private DeptService deptService;

	//部门列表
	@RequestMapping("dept/list")
	public ResResult deptList(Dept dept) {
		List<Dept> list = this.deptService.findAllDepts(dept);;
		return ResResult.success(list);
	}

	//部门树
	@RequestMapping("/notauth/dept/tree")
	public ResResult getDeptTree() {
		Tree<Dept> tree = this.deptService.getDeptTree();
		return ResResult.success(tree);
	}

	//根据deptId，查询指定部门信息
	@RequestMapping("notauth/dept/getDept")
	public ResResult getDept(Long deptId) {
		Dept dept = this.deptService.findById(deptId);
		return ResResult.success(dept);
	}
	
	//新增部门
	@RequestMapping("dept/add")
	public ResResult addRole(Dept dept) {
		Dept oldDept = this.deptService.findByName(dept.getDeptName().trim());
		if(oldDept != null){
			return ResResult.error("部门名称已存在！");
		}
		this.deptService.addDept(dept);
		return ResResult.success();
	}

	//修改部门
	@RequestMapping("dept/update")
	public ResResult updateRole(Dept dept) {
		this.deptService.updateDept(dept);
		return ResResult.success();
	}

	//删除部门
	@RequestMapping("dept/delete")
	public ResResult deleteDept(Long id) {
		this.deptService.deleteDepts(id);
		return ResResult.success();
	}

}
