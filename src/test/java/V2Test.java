import com.alibaba.fastjson.JSON;
import nirvana.cash.loan.privilege.system.domain.Menu;
import nirvana.cash.loan.privilege.system.domain.Role;
import nirvana.cash.loan.privilege.system.domain.vo.LeftMenuVo;
import nirvana.cash.loan.privilege.system.service.MenuService;
import nirvana.cash.loan.privilege.system.service.RoleService;
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
    public void findUserMenus(){
        List<LeftMenuVo> menus=menuService.findUserMenus();
        System.err.println(JSON.toJSONString(menus));
    }

}
