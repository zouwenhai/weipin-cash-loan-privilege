package nirvana.cash.loan.privilege.controller.springmvc;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.enums.RoleEnum;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.controller.springmvc.base.BaseController;
import nirvana.cash.loan.privilege.domain.Role;
import nirvana.cash.loan.privilege.service.RoleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

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
		Role role = this.roleService.findRoleWithMenus(roleId);
		return ResResult.success(role);
	}

	//新增角色
	@RequestMapping("role/add")
	public ResResult addRole(Role role) {
		if(StringUtils.isBlank(role.getMenuIds2())){
			return ResResult.error("请选择菜单权限！");
		}
		List<Long> menuIds = Arrays.asList(role.getMenuIds2().split(",")).stream().map(t->Long.valueOf(t))
				.collect(Collectors.toList());
		Role oldRoleName2 = this.roleService.findByRoleName2(role.getRoleName2());
		if(oldRoleName2 != null){
			return ResResult.error("您选择的角色已存在！");
		}
		this.roleService.addRole(role, menuIds);
		return ResResult.success();
	}

	//修改角色
	@RequestMapping("role/update")
	public ResResult updateRole(Role role,ServerHttpRequest request) {
		if(StringUtils.isBlank(role.getMenuIds2())){
			return ResResult.error("请选择菜单权限！");
		}
		List<Long> menuIds = Arrays.asList(role.getMenuIds2().split(",")).stream().map(t->Long.valueOf(t))
				.collect(Collectors.toList());
		Long loginUserId=this.getLoginUser(request).getUserId();
		this.roleService.updateRole(role,menuIds,loginUserId);
		return ResResult.success();
	}

	//删除角色
	@RequestMapping("role/delete")
	public ResResult deleteRoles(Long ids,ServerHttpRequest request) {
		Long loginUserId=this.getLoginUser(request).getUserId();
		return this.roleService.deleteRoles(ids,loginUserId);
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
