package nirvana.cash.loan.privilege.controller.springmvc;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.controller.springmvc.base.BaseController;
import nirvana.cash.loan.privilege.domain.Dept;
import nirvana.cash.loan.privilege.domain.Role;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.service.DeptService;
import nirvana.cash.loan.privilege.service.RoleService;
import nirvana.cash.loan.privilege.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/privilige")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DeptService deptService;

    //用户列表
    @RequestMapping("user/list")
    public ResResult userList(QueryRequest request, User user) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<User> userList = userService.findUserWithDept(user);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        //所属部门名称
        if (!CollectionUtils.isEmpty(pageInfo.getList())) {
            List<Dept> deptList = deptService.findAllDepts(new Dept());
            if (CollectionUtils.isEmpty(deptList)) {
                return ResResult.success(getDataTable(pageInfo));
            }
            Map<String, String> deptmap = deptList.stream().collect(Collectors.toMap(i -> i.getDeptId().toString(), i -> i.getDeptName()));
            userList.forEach(t -> {
                Set<String> itemDeptNameSet = new HashSet<>();
                String deptIds = t.getDeptId();
                if (StringUtils.isNotBlank(deptIds)) {
                    List<String> deptNameList = Arrays.asList(deptIds.split(",")).stream()
                            .map(x ->deptmap.get(x))
                            .collect(Collectors.toList());
                    itemDeptNameSet.addAll(deptNameList);
                }
                t.setDeptName(StringUtils.join(itemDeptNameSet, ","));
            });
        }
        return ResResult.success(getDataTable(pageInfo));
    }

    //查询登录用信息
    @RequestMapping("notauth/user")
    public ResResult index(ServerHttpRequest request) {
        User user = this.getLoginUser(request);
        return ResResult.success(user);
    }

    //根据用户ID，查询指定用户信息
    @RequestMapping("notauth/user/getUser")
    public ResResult getUser(Long userId) {
        User user = this.userService.findById(userId);
        return ResResult.success(user);
    }


    //新增用户
    @RequestMapping("user/add")
    public ResResult addUser(User user, ServerHttpRequest request) {
        String rolesSelect = user.getRoleIds2();
        if (StringUtils.isBlank(rolesSelect)) {
            return ResResult.error("请选择用户角色！");
        }
        List<Long> roleIdList = Arrays.asList(rolesSelect.split(",")).stream().map(t -> Long.valueOf(t))
                .collect(Collectors.toList());
        user.setUsername(user.getUsername().trim());
        User oldUser = this.userService.findByName(user.getUsername());
        if (oldUser != null) {
            return ResResult.error("登录名不可用！");
        }
        User loginUser = getLoginUser(request);
        return this.userService.addUser(user, roleIdList, loginUser);
    }

    //修改用户
    @RequestMapping("user/update")
    public ResResult updateUser(User user, ServerHttpRequest request) {
        String rolesSelect = user.getRoleIds2();
        if (StringUtils.isBlank(rolesSelect)) {
            return ResResult.error("请选择用户角色！");
        }
        List<Long> roleIdList = Arrays.asList(rolesSelect.split(",")).stream().map(t -> Long.valueOf(t))
                .collect(Collectors.toList());
        User loginUser = this.getLoginUser(request);
        Long loginUserId = loginUser.getUserId();
        String username = loginUser.getUsername();
        this.userService.updateUser(user, roleIdList, loginUserId, username);
        return ResResult.success();
    }

    //删除用户
    @RequestMapping("user/delete")
    public ResResult deleteUser(Integer id) {
        this.userService.deleteUser(id);
        return ResResult.success();
    }

    //修改密码
    @RequestMapping("user/updatePassword")
    public ResResult updatePassword(String newpassword, Long userId) {
        User user = new User();
        user.setUserId(userId);
        this.userService.updatePassword(newpassword, userId);
        return ResResult.success();
    }

    //校验登录名
    @RequestMapping("notauth/user/checkUserName")
    public ResResult checkUserName(String userName) {
        User user = this.userService.findByName(userName.trim());
        if (user != null) {
            return ResResult.error("登录名不可用！");
        }
        return ResResult.success("登录名可用！");
    }

    //角色列表,新增用户时使用
    @RequestMapping("/notauth/user/roleList")
    public ResResult roleList() {
        PageHelper.startPage(1, Integer.MAX_VALUE);
        List<Role> list = roleService.findAllRole(new Role());
        PageInfo<Role> pageInfo = new PageInfo<>(list);
        return ResResult.success(getDataTable(pageInfo));
    }

    //根据用户ID，查询指定用户信息
    @RequestMapping("/user/findByLoginName")
    public ResResult findByLoginName(String loginName) {
        if(StringUtils.isBlank(loginName)){
            return ResResult.error("登录名不能为空");
        }
        User user = userService.findByName(loginName);
        if(user != null){
            return ResResult.success(user);
        }
        return ResResult.error("用户不存在");
    }

}
