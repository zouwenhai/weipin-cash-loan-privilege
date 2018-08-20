package nirvana.cash.loan.privilege.system.controller;

import nirvana.cash.loan.privilege.common.controller.BaseController;
import nirvana.cash.loan.privilege.common.domain.Tree;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.system.domain.Dept;
import nirvana.cash.loan.privilege.system.service.DeptService;
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
		List<Dept> list = null;
		try {
			 list = this.deptService.findAllDepts(dept);
		} catch (Exception e) {
			e.printStackTrace();
			return ResResult.error();
		}
		return ResResult.success(list);
	}

	//部门树
	@RequestMapping("/notauth/dept/tree")
	public ResResult getDeptTree() {
		try {
			Tree<Dept> tree = this.deptService.getDeptTree();
			return ResResult.success(tree);
		} catch (Exception e) {
			e.printStackTrace();
			return ResResult.error("获取部门列表失败！");
		}
	}

	//根据deptId，查询指定部门信息
	@RequestMapping("notauth/dept/getDept")
	public ResResult getDept(Long deptId) {
		try {
			Dept dept = this.deptService.findById(deptId);
			return ResResult.success(dept);
		} catch (Exception e) {
			e.printStackTrace();
			return ResResult.error("获取部门信息失败！");
		}
	}
	
	//新增部门
	@RequestMapping("dept/add")
	public ResResult addRole(Dept dept) {
		try {
			Dept oldDept = this.deptService.findByName(dept.getDeptName().trim());
			if(oldDept != null){
				return ResResult.error("部门名称已存在！");
			}
			this.deptService.addDept(dept);
			return ResResult.success();
		} catch (Exception e) {
			logger.error("部门管理|新增部门|执行异常:{}",e);
			return ResResult.error("新增部门失败！");
		}
	}

	//修改部门
	@RequestMapping("dept/update")
	public ResResult updateRole(Dept dept) {
		try {
			this.deptService.updateDept(dept);
			return ResResult.success();
		} catch (Exception e) {
			logger.error("部门管理|修改部门|执行异常:{}",e);
			return ResResult.error("修改部门失败！");
		}
	}

	//删除部门
	@RequestMapping("dept/delete")
	public ResResult deleteDept(String id) {
		try {
			this.deptService.deleteDepts(id);
			return ResResult.success();
		} catch (Exception e) {
			logger.error("部门管理|删除部门|执行异常:{}",e);
			return ResResult.error("删除部门失败！");
		}
	}

}
