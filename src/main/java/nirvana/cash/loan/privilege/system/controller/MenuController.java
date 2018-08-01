package nirvana.cash.loan.privilege.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import nirvana.cash.loan.privilege.common.controller.BaseController;
import nirvana.cash.loan.privilege.common.domain.Tree;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.common.util.TreeUtils;
import nirvana.cash.loan.privilege.system.domain.Menu;
import nirvana.cash.loan.privilege.system.domain.User;
import nirvana.cash.loan.privilege.system.domain.vo.LeftMenuVo;
import nirvana.cash.loan.privilege.system.service.MenuService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/privilige")
public class MenuController extends BaseController {
    @Autowired
    private MenuService menuService;

    //菜单列表
    @RequestMapping("menu/list")
    public ResResult menuList(Menu menu) {
        try {
            return ResResult.success(this.menuService.findAllMenus(menu));
        } catch (Exception e) {
            e.printStackTrace();
            return ResResult.error();
        }
    }

    //根据menuId，查询指定菜单信息
    @RequestMapping("notauth/menu/getMenu")
    public ResResult getMenu(Long menuId) {
        try {
            Menu menu = this.menuService.findById(menuId);
            return ResResult.success(menu);
        } catch (Exception e) {
            e.printStackTrace();
            return ResResult.error("获取信息失败！");
        }
    }

    //查询菜单树（非按钮级别）
    @RequestMapping("notauth/menu/tree")
    public ResResult getMenuTree() {
        try {
            Tree<Menu> tree = this.menuService.getMenuTree();
            return ResResult.success(tree);
        } catch (Exception e) {
            e.printStackTrace();
            return ResResult.error("获取菜单列表失败！");
        }
    }

    //新增
    @RequestMapping("menu/add")
    public ResResult addMenu(Menu menu) {
        String name;
        if (Menu.TYPE_MENU.equals(menu.getType()))
            name = "菜单";
        else
            name = "按钮";
        try {
            this.menuService.addMenu(menu);
            return ResResult.success();
        } catch (Exception e) {
            logger.error("菜单管理|新增菜单|执行异常:{}",e);
            return ResResult.error("新增" + name + "失败！");
        }
    }

    //修改菜单
    @RequestMapping("menu/update")
    public ResResult updateMenu(Menu menu) {
        String name;
        if (Menu.TYPE_MENU.equals(menu.getType()))
            name = "菜单";
        else
            name = "按钮";
        try {
            this.menuService.updateMenu(menu);
            return ResResult.success();
        } catch (Exception e) {
            logger.error("菜单管理|修改菜单|执行异常:{}",e);
            return ResResult.error("修改" + name + "失败！");
        }
    }

    //删除菜单
    @RequestMapping("menu/delete")
    public ResResult deleteMenus(String menuIds) {
        try {
            this.menuService.deleteMeuns(menuIds);
            return ResResult.success();
        } catch (Exception e) {
            logger.error("菜单管理|删除菜单|执行异常:{}",e);
            return ResResult.error("删除失败！");
        }
    }

    //查询菜单树（包含按钮级别）
    @RequestMapping("notauth/menu/menuButtonTree")
    public ResResult getMenuButtonTree() {
        try {
            Tree<Menu> tree = this.menuService.getMenuButtonTree();
            return ResResult.success(tree);
        } catch (Exception e) {
            e.printStackTrace();
            return ResResult.error("获取菜单列表失败！");
        }
    }

    //查询指定用户菜单树（非按钮级别）
    @RequestMapping("notauth/menu/getUserMenu")
    public ResResult getUserMenu(HttpServletRequest request) {
        try {
            User user=this.getLoginUser(request);
            if(user == null){
                return ResResult.error("登录超时!",ResResult.LOGIN_SESSION_TIMEOUT);
            }
            String userTreeKey = "userTree-" + user.getUsername();
            String treestr=redisService.get(userTreeKey,String.class);
            return ResResult.success(JSON.parseObject(treestr));
        } catch (Exception e) {
            e.printStackTrace();
            return ResResult.error("获取用户菜单失败！");
        }
    }

    //查询用户权限
    @RequestMapping("/notauth/menu/findUserPermissions")
    public ResResult findUserPermissions(HttpServletRequest request) {
        try {
            User user=this.getLoginUser(request);
            if(user == null){
                return ResResult.error("登录超时!",ResResult.LOGIN_SESSION_TIMEOUT);
            }
            String userPermissions = redisService.get("userPermissions-" + user.getUsername(),String.class);
            List<Menu> permissionList = JSONObject.parseArray(userPermissions, Menu.class);
            return ResResult.success(permissionList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResResult.error("查询用户权限失败！");
        }
    }

    //查询左侧菜单列表
    @RequestMapping("/notauth/menu/findLeftMenuList")
    public ResResult findLeftMenuList(HttpServletRequest request) {
        try {
            User user=this.getLoginUser(request);
            if(user == null){
                return ResResult.error("登录超时!",ResResult.LOGIN_SESSION_TIMEOUT);
            }
            List<LeftMenuVo> menus=menuService.findUserMenus();
            //转换为树结构
            List<Tree<LeftMenuVo>> trees = new ArrayList<>();
            for (LeftMenuVo menu : menus) {
                Tree<LeftMenuVo> tree = new Tree<>();
                tree.setId(menu.getMenuId().toString());
                tree.setParentId(menu.getParentId().toString());
                tree.setText(menu.getMenuName());
                tree.setIcon(menu.getIcon());
                tree.setUrl(menu.getUrl());
                trees.add(tree);
            }
            return ResResult.success(TreeUtils.build(trees));
        } catch (Exception e) {
            e.printStackTrace();
            return ResResult.error("查询左侧菜单列表失败！");
        }
    }
}
