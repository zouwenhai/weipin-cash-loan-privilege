import com.alibaba.fastjson.JSON;
import nirvana.cash.loan.privilege.domain.Menu;
import nirvana.cash.loan.privilege.domain.Role;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.vo.LeftMenuVo;
import nirvana.cash.loan.privilege.service.MenuService;
import nirvana.cash.loan.privilege.service.RoleService;
import nirvana.cash.loan.privilege.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by Administrator on 2018/10/31.
 */
public class V2Test extends BaseTest {

    @Autowired
    private RoleService roleService;
    @Autowired
    private MenuService menuService;

    @Autowired
    private UserService userService;

    @Test
    public void findByRoleName2() {
        Role role = roleService.findByRoleName2("超级管理员");
        System.err.println(JSON.toJSONString(role));
    }

    @Test
    public void findByParentId() {
        Long parentId = 0L;
        List<Menu> menuList = menuService.findByParentId(parentId);
        System.err.println(JSON.toJSONString(menuList));
    }

    @Test
    public void resetOrderNum() {
        Long parentId = 0L;
        menuService.resetOrderNum(parentId);
        System.err.println("done");
    }

    @Test
    public void findUserMenus() {
        List<LeftMenuVo> menus = menuService.findUserMenus();
        System.err.println(JSON.toJSONString(menus));
    }

    @Test
    public void findAllMenus() {
        Menu menu = new Menu();
//        menu.setMenuName("系统管理");
        List<Menu> list = this.menuService.findAllMenus(menu);
        System.err.println(JSON.toJSONString(list));
    }

    @Test
    public void findUserWithDept() {
        User user = new User();
        List<User> list = this.userService.findUserWithDept(user);
        System.err.println(JSON.toJSONString(list));
    }

}
