package nirvana.cash.loan.privilege.system.controller;

import nirvana.cash.loan.privilege.common.controller.BaseController;
import nirvana.cash.loan.privilege.common.domain.ResponseBo;
import nirvana.cash.loan.privilege.common.domain.Tree;
import nirvana.cash.loan.privilege.system.domain.Menu;
import nirvana.cash.loan.privilege.system.domain.User;
import nirvana.cash.loan.privilege.system.service.MenuService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/privilige")
public class MenuController extends BaseController {
    @Autowired
    private MenuService menuService;

    @RequestMapping("menu/menu")
    public ResponseBo getMenu(String userName) {
        try {
            List<Menu> menus = this.menuService.findUserMenus(userName);
            return ResponseBo.ok(menus);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("获取菜单失败！");
        }
    }

    @RequestMapping("menu/getMenu")
    public ResponseBo getMenu(Long menuId) {
        try {
            Menu menu = this.menuService.findById(menuId);
            return ResponseBo.ok(menu);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("获取信息失败，请联系网站管理员！");
        }
    }

    @RequestMapping("menu/menuButtonTree")
    public ResponseBo getMenuButtonTree() {
        try {
            Tree<Menu> tree = this.menuService.getMenuButtonTree();
            return ResponseBo.ok(tree);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("获取菜单列表失败！");
        }
    }

    @RequestMapping("menu/tree")
    public ResponseBo getMenuTree() {
        try {
            Tree<Menu> tree = this.menuService.getMenuTree();
            return ResponseBo.ok(tree);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("获取菜单列表失败！");
        }
    }

    @RequestMapping("menu/getUserMenu")
    public ResponseBo getUserMenu(HttpServletRequest request) {
        try {
            User user=this.getLoginUser(request);
            Tree<Menu> tree = this.menuService.getUserMenu(user.getUsername());
            return ResponseBo.ok(tree);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("获取用户菜单失败！");
        }
    }

    @RequestMapping("menu/list")
    public List<Menu> menuList(Menu menu) {
        try {
            return this.menuService.findAllMenus(menu);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping("menu/excel")
    public ResponseBo menuExcel(Menu menu) {
        try {
            List<Menu> list = this.menuService.findAllMenus(menu);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("导出Excel失败，请联系网站管理员！");
        }
    }

    @RequestMapping("menu/csv")
    public ResponseBo menuCsv(Menu menu) {
        try {
            List<Menu> list = this.menuService.findAllMenus(menu);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("导出Csv失败，请联系网站管理员！");
        }
    }

    @RequestMapping("menu/checkMenuName")
    public boolean checkMenuName(String menuName, String type, String oldMenuName) {
        if (StringUtils.isNotBlank(oldMenuName) && menuName.equalsIgnoreCase(oldMenuName)) {
            return true;
        }
        Menu result = this.menuService.findByNameAndType(menuName, type);
        return result == null;
    }

    @RequestMapping("menu/add")
    public ResponseBo addMenu(Menu menu) {
        String name;
        if (Menu.TYPE_MENU.equals(menu.getType()))
            name = "菜单";
        else
            name = "按钮";
        try {
            this.menuService.addMenu(menu);
            return ResponseBo.ok("新增" + name + "成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("新增" + name + "失败，请联系网站管理员！");
        }
    }

    @RequestMapping("menu/delete")
    public ResponseBo deleteMenus(String ids) {
        try {
            this.menuService.deleteMeuns(ids);
            return ResponseBo.ok("删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("删除失败，请联系网站管理员！");
        }
    }

    @RequestMapping("menu/update")
    public ResponseBo updateMenu(Menu menu) {
        String name;
        if (Menu.TYPE_MENU.equals(menu.getType()))
            name = "菜单";
        else
            name = "按钮";
        try {
            this.menuService.updateMenu(menu);
            return ResponseBo.ok("修改" + name + "成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error("修改" + name + "失败，请联系网站管理员！");
        }
    }
}
