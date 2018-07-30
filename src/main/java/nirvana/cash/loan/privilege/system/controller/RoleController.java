package nirvana.cash.loan.privilege.system.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import nirvana.cash.loan.privilege.common.controller.BaseController;
import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.domain.ResponseBo;
import nirvana.cash.loan.privilege.system.domain.Role;
import nirvana.cash.loan.privilege.system.service.RoleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/privilige")
public class RoleController extends BaseController {

	@Autowired
	private RoleService roleService;

	@RequestMapping("role/list")
	public Map<String, Object> roleList(QueryRequest request, Role role) {
		PageHelper.startPage(request.getPageNum(), request.getPageSize());
		List<Role> list = this.roleService.findAllRole(role);
		PageInfo<Role> pageInfo = new PageInfo<>(list);
		return getDataTable(pageInfo);
	}
	
	@RequestMapping("role/excel")
	public ResponseBo roleExcel(Role role) {
		try {
			List<Role> list = this.roleService.findAllRole(role);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("导出Excel失败，请联系网站管理员！");
		}
	}

	@RequestMapping("role/csv")
	public ResponseBo roleCsv(Role role){
		try {
			List<Role> list = this.roleService.findAllRole(role);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("导出Csv失败，请联系网站管理员！");
		}
	}
	
	@RequestMapping("role/getRole")
	public ResponseBo getRole(Long roleId) {
		try {
			Role role = this.roleService.findRoleWithMenus(roleId);
			return ResponseBo.ok(role);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("获取角色信息失败，请联系网站管理员！");
		}
	}

	@RequestMapping("role/checkRoleName")
	public boolean checkRoleName(String roleName, String oldRoleName) {
		if (StringUtils.isNotBlank(oldRoleName) && roleName.equalsIgnoreCase(oldRoleName)) {
			return true;
		}
		Role result = this.roleService.findByName(roleName);
		return result == null;
	}

	@RequestMapping("role/add")
	public ResponseBo addRole(Role role, Long[] menuId) {
		try {
			this.roleService.addRole(role, menuId);
			return ResponseBo.ok("新增角色成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("新增角色失败，请联系网站管理员！");
		}
	}

	@RequestMapping("role/delete")
	public ResponseBo deleteRoles(String ids) {
		try {
			this.roleService.deleteRoles(ids);
			return ResponseBo.ok("删除角色成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("删除角色失败，请联系网站管理员！");
		}
	}

	@RequestMapping("role/update")
	public ResponseBo updateRole(Role role, Long[] menuId) {
		try {
			this.roleService.updateRole(role, menuId);
			return ResponseBo.ok("修改角色成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseBo.error("修改角色失败，请联系网站管理员！");
		}
	}
}
