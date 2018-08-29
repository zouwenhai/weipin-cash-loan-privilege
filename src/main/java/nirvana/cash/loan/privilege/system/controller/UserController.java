package nirvana.cash.loan.privilege.system.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import nirvana.cash.loan.privilege.common.controller.BaseController;
import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.system.domain.Role;
import nirvana.cash.loan.privilege.system.domain.User;
import nirvana.cash.loan.privilege.system.service.RoleService;
import nirvana.cash.loan.privilege.system.service.UserService;
import nirvana.cash.loan.privilege.web.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/privilige")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    //用户列表
    @RequestMapping("user/list")
    public ResResult userList(QueryRequest request, User user) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<User> list = this.userService.findUserWithDept(user);
        PageInfo<User> pageInfo = new PageInfo<>(list);
        return ResResult.success(getDataTable(pageInfo));
    }

    //查询登录用信息
    @RequestMapping("notauth/user")
    public ResResult index(HttpServletRequest request, HttpServletResponse response) {
        User user = this.getLoginUser(request);
        return ResResult.success(user);
    }

    //根据用户ID，查询指定用户信息
    @RequestMapping("notauth/user/getUser")
    public ResResult getUser(Long userId) {
        try {
            User user = this.userService.findById(userId);
            return ResResult.success(user);
        } catch (Exception e) {
            return ResResult.error("获取用户信息失败！");
        }
    }


    //新增用户
    @RequestMapping("user/add")
    public ResResult addUser(User user, Long[] roles) {
        try {
            user.setUsername(user.getUsername().trim());
            User oldUser = this.userService.findByName(user.getUsername());
            if (oldUser != null) {
                return ResResult.error("登录名不可用！");
            }
            if(roles.length == 0){
                return ResResult.error("请选择用户角色！");
            }
            return this.userService.addUser(user, roles);
        }
        catch (BizException e) {
            logger.error("用户管理|新增用户|执行异常:{}", e);
            return ResResult.error(e.getMessage());
        }
        catch (Exception e) {
            logger.error("用户管理|新增用户|执行异常:{}", e);
            return ResResult.error("新增用户失败！");
        }
    }

    //修改用户
    @RequestMapping("user/update")
    public ResResult updateUser(User user, Long[] rolesSelect) {
        try {
            this.userService.updateUser(user, rolesSelect);
            return ResResult.success();
        }
        catch (BizException e) {
            logger.error("用户管理|修改用户|执行异常:{}", e);
            return ResResult.error(e.getMessage());
        }
        catch (Exception e) {
            logger.error("用户管理|修改用户|执行异常:{}", e);
            return ResResult.error("修改用户失败！");
        }
    }

    //删除用户
    @RequestMapping("user/delete")
    public ResResult deleteUser(Integer id) {
        try {
            this.userService.deleteUser(id);
            return ResResult.success();
        } catch (Exception e) {
            logger.error("用户管理|删除用户|执行异常:{}", e);
            return ResResult.error("删除用户失败！");
        }
    }

    //修改密码
    @RequestMapping("user/updatePassword")
    public ResResult updatePassword(HttpServletRequest request, HttpServletResponse response, String newpassword,Long userId) {
        try {
            User user = new User();
            user.setUserId(userId);
            this.userService.updatePassword(newpassword, userId);
            return ResResult.success();
        } catch (Exception e) {
            logger.error("用户管理|修改密码|执行异常:{}", e);
            return ResResult.error("修改密码失败！");
        }
    }

    //校验登录名
    @RequestMapping("notauth/user/checkUserName")
    public ResResult checkUserName(String userName) {
        try {
            User user = this.userService.findByName(userName.trim());
            if (user != null) {
                return ResResult.error("登录名不可用！");
            }
            return ResResult.success("登录名可用！");
        } catch (Exception e) {
            logger.error("用户管理|校验登录名|执行异常:{}", e);
            return ResResult.error("校验登录名失败！");
        }
    }

    //角色列表,新增用户时使用
    @RequestMapping("/notauth/user/roleList")
    public ResResult roleList() {
        PageHelper.startPage(1, Integer.MAX_VALUE);
        List<Role> list = roleService.findAllRole(new Role());
        PageInfo<Role> pageInfo = new PageInfo<>(list);
        return ResResult.success(getDataTable(pageInfo));
    }

}
