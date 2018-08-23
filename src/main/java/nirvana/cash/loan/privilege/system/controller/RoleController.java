package nirvana.cash.loan.privilege.system.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import nirvana.cash.loan.privilege.common.controller.BaseController;
import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.enums.RoleEnum;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.system.domain.Role;
import nirvana.cash.loan.privilege.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/privilige")
public class RoleController extends BaseController {

	@Autowired
	private RoleService roleService;

	//角色列表
	@RequestMapping("role/list")
	public ResResult roleList(QueryRequest request, Role role) {
		PageHelper.startPage(request.getPageNum(), request.getPageSize());
		List<Role> list = this.roleService.findAllRole(role);
		PageInfo<Role> pageInfo = new PageInfo<>(list);
		return ResResult.success(getDataTable(pageInfo));
	}

	//根据角色ID，查找指定角色信息
	@RequestMapping("notauth/role/getRole")
	public ResResult getRole(Long roleId) {
		try {
			Role role = this.roleService.findRoleWithMenus(roleId);
			return ResResult.success(role);
		} catch (Exception e) {
			return ResResult.error("获取角色信息失败！");
		}
	}

	//新增角色
	@RequestMapping("role/add")
	public ResResult addRole(Role role, Long[] menuId) {
		try {
			Role oldRole = this.roleService.findByCode(role.getRoleCode());
			if(oldRole != null){
				return ResResult.error("您选择的角色已存在！");
			}
			this.roleService.addRole(role, menuId);
			return ResResult.success();
		} catch (Exception e) {
			logger.error("角色管理|新增角色|执行异常:{}",e);
			return ResResult.error("新增角色失败！");
		}
	}

	//修改角色
	@RequestMapping("role/update")
	public ResResult updateRole(Role role, Long[] menuId) {
		try {
			this.roleService.updateRole(role, menuId);
			return ResResult.success();
		} catch (Exception e) {
			logger.error("角色管理|修改角色|执行异常:{}",e);
			return ResResult.error("修改角色失败！");
		}
	}

	//删除角色
	@RequestMapping("role/delete")
	public ResResult deleteRoles(String ids) {
		try {
			this.roleService.deleteRoles(ids);
			return ResResult.success();
		} catch (Exception e) {
			logger.error("角色管理|删除角色|执行异常:{}",e);
			return ResResult.error("删除角色失败！");
		}
	}

	//角色配置下拉列表
	@RequestMapping("notauth/role/selectList")
	public ResResult roleList() {
		List<Map<String, String>> list = new ArrayList<>();
		for (RoleEnum roleEnum : RoleEnum.values()) {
			Map itemMap = new HashMap<>();
			itemMap.put("roleName", roleEnum.getName());
			itemMap.put("roleCode", roleEnum.getCode());
			list.add(itemMap);
		}
		return ResResult.success(list);
	}
}
