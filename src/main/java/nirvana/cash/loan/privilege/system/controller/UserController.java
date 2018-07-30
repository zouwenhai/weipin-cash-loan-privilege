package nirvana.cash.loan.privilege.system.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import nirvana.cash.loan.privilege.common.controller.BaseController;
import nirvana.cash.loan.privilege.common.domain.QueryRequest;
import nirvana.cash.loan.privilege.common.domain.ResponseBo;
import nirvana.cash.loan.privilege.common.util.MD5Utils;
import nirvana.cash.loan.privilege.system.domain.User;
import nirvana.cash.loan.privilege.system.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/privilige")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    private static final String ON = "on";

    @RequestMapping("user")
    public ResponseBo index(HttpServletRequest request, HttpServletResponse response) {
        User user = this.getLoginUser(request);
        return ResponseBo.ok(user);
    }

    @RequestMapping("user/checkUserName")
    public boolean checkUserName(String username, String oldusername) {
        if (StringUtils.isNotBlank(oldusername) && username.equalsIgnoreCase(oldusername)) {
            return true;
        }
        User result = this.userService.findByName(username);
        return result == null;
    }

    @RequestMapping("user/getUser")
    public ResponseBo getUser(Long userId) {
        try {
            User user = this.userService.findById(userId);
            return ResponseBo.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("获取用户信息失败，请联系网站管理员！");
        }
    }

    @RequestMapping("user/list")
    public Map<String, Object> userList(QueryRequest request, User user) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<User> list = this.userService.findUserWithDept(user);
        PageInfo<User> pageInfo = new PageInfo<>(list);
        return getDataTable(pageInfo);
    }

    @RequestMapping("user/excel")
    public ResponseBo userExcel(User user) {
        try {
            List<User> list = this.userService.findUserWithDept(user);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("导出Excel失败，请联系网站管理员！");
        }
    }

    @RequestMapping("user/csv")
    public ResponseBo userCsv(User user) {
        try {
            List<User> list = this.userService.findUserWithDept(user);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("导出Csv失败，请联系网站管理员！");
        }
    }

    @RequestMapping("user/regist")
    public ResponseBo regist(User user) {
        try {
            User result = this.userService.findByName(user.getUsername());
            if (result != null) {
                return ResponseBo.warn("该用户名已被使用！");
            }
            this.userService.registUser(user);
            return ResponseBo.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("注册失败，请联系网站管理员！");
        }
    }

    @RequestMapping("user/theme")
    public ResponseBo updateTheme(User user) {
        try {
            this.userService.updateTheme(user.getTheme(), user.getUsername());
            return ResponseBo.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error();
        }
    }

    @RequestMapping("user/add")
    public ResponseBo addUser(User user, Long[] roles) {
        try {
            if (ON.equalsIgnoreCase(user.getStatus()))
                user.setStatus("1");
            else
                user.setStatus("0");
            this.userService.addUser(user, roles);
            return ResponseBo.ok("新增用户成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("新增用户失败，请联系网站管理员！");
        }
    }

    @RequestMapping("user/update")
    public ResponseBo updateUser(User user, Long[] rolesSelect) {
        try {
            if (ON.equalsIgnoreCase(user.getStatus()))
                user.setStatus("1");
            else
                user.setStatus("0");
            this.userService.updateUser(user, rolesSelect);
            return ResponseBo.ok("修改用户成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("修改用户失败，请联系网站管理员！");
        }
    }

    @RequestMapping("user/delete")
    public ResponseBo deleteUsers(String ids) {
        try {
            this.userService.deleteUsers(ids);
            return ResponseBo.ok("删除用户成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("删除用户失败，请联系网站管理员！");
        }
    }

    @RequestMapping("user/checkPassword")
    public boolean checkPassword(HttpServletRequest request, HttpServletResponse response, String password) {
        User user = this.getLoginUser(request);
        String encrypt = MD5Utils.encrypt(user.getUsername().toLowerCase(), password);
        return user.getPassword().equals(encrypt);
    }

    @RequestMapping("user/updatePassword")
    public ResponseBo updatePassword(HttpServletRequest request, HttpServletResponse response, String newpassword) {
        try {
            User user = this.getLoginUser(request);
            this.userService.updatePassword(newpassword, user);
            return ResponseBo.ok("更改密码成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("更改密码失败，请联系网站管理员！");
        }
    }

    @RequestMapping("user/profile")
    public ResponseBo profileIndex(HttpServletRequest request, HttpServletResponse response) {
        User user = this.getLoginUser(request);
        user = this.userService.findUserProfile(user);
        String ssex = user.getSsex();
        if (User.SEX_MALE.equals(ssex)) {
            user.setSsex("性别：男");
        } else if (User.SEX_FEMALE.equals(ssex)) {
            user.setSsex("性别：女");
        } else {
            user.setSsex("性别：保密");
        }
        return ResponseBo.ok(user);
    }

    @RequestMapping("user/getUserProfile")
    public ResponseBo getUserProfile(Long userId) {
        try {
            User user = new User();
            user.setUserId(userId);
            return ResponseBo.ok(this.userService.findUserProfile(user));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("获取用户信息失败，请联系网站管理员！");
        }
    }

    @RequestMapping("user/updateUserProfile")
    public ResponseBo updateUserProfile(User user) {
        try {
            this.userService.updateUserProfile(user);
            return ResponseBo.ok("更新个人信息成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("获取用户信息失败，请联系网站管理员！");
        }
    }

    @RequestMapping("user/changeAvatar")
    public ResponseBo changeAvatar(HttpServletRequest request, HttpServletResponse response, String imgName) {
        try {
            String[] img = imgName.split("/");
            String realImgName = img[img.length - 1];
            User user = this.getLoginUser(request);
            user.setAvatar(realImgName);
            this.userService.updateNotNull(user);
            return ResponseBo.ok("更新头像成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("更新头像失败，请联系网站管理员！");
        }
    }
}
