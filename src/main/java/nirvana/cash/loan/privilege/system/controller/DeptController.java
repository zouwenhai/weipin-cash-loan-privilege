package nirvana.cash.loan.privilege.system.controller;

import nirvana.cash.loan.privilege.common.domain.ResponseBo;
import nirvana.cash.loan.privilege.common.domain.Tree;
import nirvana.cash.loan.privilege.system.domain.Dept;
import nirvana.cash.loan.privilege.system.service.DeptService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/privilige")
public class DeptController {

	@Autowired
	private DeptService deptService;

	@RequestMapping("/dept/tree")
	public ResponseBo getDeptTree() {
		try {
			Tree<Dept> tree = this.deptService.getDeptTree();
			return ResponseBo.ok(tree);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("获取部门列表失败！");
		}
	}

	@RequestMapping("dept/getDept")
	public ResponseBo getDept(Long deptId) {
		try {
			Dept dept = this.deptService.findById(deptId);
			return ResponseBo.ok(dept);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("获取部门信息失败，请联系网站管理员！");
		}
	}
	
	@RequestMapping("dept/list")
	public List<Dept> deptList(Dept dept) {
		try {
			return this.deptService.findAllDepts(dept);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping("dept/excel")
	public ResponseBo deptExcel(Dept dept) {
		try {
			List<Dept> list = this.deptService.findAllDepts(dept);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("导出Excel失败，请联系网站管理员！");
		}
	}

	@RequestMapping("dept/csv")
	public ResponseBo deptCsv(Dept dept){
		try {
			List<Dept> list = this.deptService.findAllDepts(dept);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("导出Csv失败，请联系网站管理员！");
		}
	}

	@RequestMapping("dept/checkDeptName")
	public boolean checkDeptName(String deptName, String oldDeptName) {
		if (StringUtils.isNotBlank(oldDeptName) && deptName.equalsIgnoreCase(oldDeptName)) {
			return true;
		}
		Dept result = this.deptService.findByName(deptName);
		return result == null;
	}

	@RequestMapping("dept/add")
	public ResponseBo addRole(Dept dept) {
		try {
			this.deptService.addDept(dept);
			return ResponseBo.ok("新增部门成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("新增部门失败，请联系网站管理员！");
		}
	}

	@RequestMapping("dept/delete")
	public ResponseBo deleteDepts(String ids) {
		try {
			this.deptService.deleteDepts(ids);
			return ResponseBo.ok("删除部门成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("删除部门失败，请联系网站管理员！");
		}
	}
	
	@RequestMapping("dept/update")
	public ResponseBo updateRole(Dept dept) {
		try {
			this.deptService.updateDept(dept);
			return ResponseBo.ok("修改部门成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("修改部门失败，请联系网站管理员！");
		}
	}
}
