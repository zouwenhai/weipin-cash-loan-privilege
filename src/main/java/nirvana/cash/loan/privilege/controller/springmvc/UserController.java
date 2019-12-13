package nirvana.cash.loan.privilege.controller.springmvc;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.enums.RoleEnum;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.controller.springmvc.base.BaseController;
import nirvana.cash.loan.privilege.domain.Dept;
import nirvana.cash.loan.privilege.domain.Role;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.fegin.facade.*;
import nirvana.cash.loan.privilege.service.DeptService;
import nirvana.cash.loan.privilege.service.RoleService;
import nirvana.cash.loan.privilege.service.UserRoleService;
import nirvana.cash.loan.privilege.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
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


    @Autowired
    private UserRoleService userRoleService;

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
                            .map(x -> deptmap.get(x))
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
        if (StringUtils.isBlank(loginName)) {
            return ResResult.error("登录名不能为空");
        }
        User user = userService.findByName(loginName);
        if (user != null) {
            return ResResult.success(user);
        }
        return ResResult.error("用户不存在");
    }

    //用户下拉列表
    @RequestMapping("notauth/user/deptUserSelect")
    public ResResult deptUserSelect(ServerHttpRequest request) {
        User user = this.getLoginUser(request);
        String deptIds = user.getDeptId();

        List<User> reslist = new ArrayList<>();

        if (StringUtils.isBlank(deptIds)) {
            reslist = userService.findAllLikeDeptId(null);
            return ResResult.success(reslist);
        }

        String[] array = deptIds.split(",");
        for (int i = 0; i < array.length; i++) {
            List<User> userList = userService.findAllLikeDeptId(Long.valueOf(array[i]));
            reslist.addAll(userList);
        }
        reslist = reslist.stream().filter(distinctByKey(t -> t.getUserId()))
                .collect(Collectors.toList());
        return ResResult.success(reslist);
    }

    /**
     * 获取借款审核人员信息
     *
     * @param isSeperate(是否分单)
     * @return
     */
    @RequestMapping("/user/getAuditUser")
    public ResResult getAuditUser(Integer isSeperate) {

        List<User> userList = userService.getAuditUser(isSeperate);
        return ResResult.success(userList);
    }

    /**
     *
     *
     * 获取复审人员信息
     *
     * @param isSeperate(是否分单)
     * @return
     */
    @RequestMapping("/user/getReviewUser")
    public ResResult getReviewUser(Integer isSeperate) {

        List<User> userList = userService.getReviewUser(isSeperate);
        return ResResult.success(userList);
    }


    /**
     * 根据userId获取借款审核人员信息
     *
     * @param userId
     * @return
     */
    @RequestMapping("/user/getAuditUserById")
    public ResResult getAuditUserById(Long userId) {
        User user = userService.getUserById(userId);
        return ResResult.success(user);
    }


    /**
     * 分页查询借款审核人员信息
     *
     * @return
     */
    @PostMapping("/user/getPageAuditUser")
    public ResResult getPageAuditUser(@RequestBody AuditUserFacade auditUserFacade) {

        PageHelper.startPage(auditUserFacade.getPageNum(), auditUserFacade.getPageSize());
        List<User> userList = userService.getAuditUser(null);
        PageInfo pageInfo = new PageInfo(userList);
        return ResResult.success(pageInfo);
    }

    /**
     * 分页查询复审人员信息
     *
     * @return
     */
    @PostMapping("/user/getPageReviewUser")
    public ResResult getPageReviewUser(@RequestBody AuditUserFacade auditUserFacade) {

        PageHelper.startPage(auditUserFacade.getPageNum(), auditUserFacade.getPageSize());
        List<User> userList = userService.getReviewUser(null);
        PageInfo pageInfo = new PageInfo(userList);
        return ResResult.success(pageInfo);
    }


    /**
     * 审核专员是否分单
     *
     * @return
     */
    @PostMapping("/user/isDivideOrder")
    public ResResult isDivideOrder(@RequestBody IsDivideOrderFacade isDivideOrderFacade) {
        if (isDivideOrderFacade == null) {
            return ResResult.error("参数为空");
        }
        userService.isDivideOrder(isDivideOrderFacade);
        return ResResult.success();
    }

    /**
     * 是否开启坐席
     *
     * @param isOpenSeatFacade
     * @return
     */
    @PostMapping(value = "/user/isOpenSeat")
    public ResResult isOpenSeat(@RequestBody IsOpenSeatFacade isOpenSeatFacade) {
        if (isOpenSeatFacade == null) {
            return ResResult.error("参数为空");
        }
        try {
            userService.isOpenSeat(isOpenSeatFacade);
        } catch (Exception e) {
            logger.error("修改失败:{}", e);
            return ResResult.error("坐席状态修改失败");
        }
        return ResResult.success();
    }

    /**
     * 添加分机号
     *
     * @param extNumberFacade
     * @return
     */
    @PostMapping(value = "/user/addExtNumber")
    public ResResult addExtNumber(@RequestBody ExtNumberFacade extNumberFacade) {
        if (extNumberFacade == null) {
            return ResResult.error("参数为空");
        }
        try {
            userService.addExtNumber(extNumberFacade);
        } catch (Exception e) {
            logger.error("修改失败:{}", e);
            return ResResult.error("分机号修改失败");
        }
        return ResResult.success();
    }

    /**
     * 修改接单上限
     *
     * @param orderTopFacade
     * @return
     */
    @PostMapping(value = "/user/updateOrderTop")
    public ResResult updateOrderTop(@RequestBody OrderTopFacade orderTopFacade) {
        if (orderTopFacade == null) {
            return ResResult.error("参数为空");
        }
        try {
            userService.updateOrderTop(orderTopFacade);

        } catch (Exception e) {
            logger.error("修改失败:{}", e);
            return ResResult.error("分机号修改失败");
        }
        return ResResult.success();
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}
