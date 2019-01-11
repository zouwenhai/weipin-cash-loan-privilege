package nirvana.cash.loan.privilege.controller.springmvc;

import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.contants.RedisKeyContant;
import nirvana.cash.loan.privilege.common.domain.Tree;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.common.util.TreeUtils;
import nirvana.cash.loan.privilege.controller.springmvc.base.BaseController;
import nirvana.cash.loan.privilege.domain.Menu;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.domain.vo.LeftMenuVo;
import nirvana.cash.loan.privilege.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/privilige")
public class MenuController extends BaseController {
    @Autowired
    private MenuService menuService;

    //菜单列表
    @RequestMapping("menu/list")
    public ResResult menuList(Menu menu) {
        return ResResult.success(this.menuService.findAllMenus(menu));
    }

    //根据menuId，查询指定菜单信息
    @RequestMapping("notauth/menu/getMenu")
    public ResResult getMenu(Long menuId) {
        Menu menu = this.menuService.findById(menuId);
        return ResResult.success(menu);
    }

    //查询菜单树（非按钮级别）
    @RequestMapping("notauth/menu/tree")
    public ResResult getMenuTree() {
        Tree<Menu> tree = this.menuService.getMenuTree();
        return ResResult.success(tree);
    }

    //新增
    @RequestMapping("menu/add")
    public ResResult addMenu(Menu menu) {
        String name;
        if (Menu.TYPE_MENU.equals(menu.getType())) {
            name = "菜单";
        } else {
            name = "按钮";
        }
        this.menuService.addMenu(menu);
        return ResResult.success();
    }

    //修改菜单
    @RequestMapping("menu/update")
    public ResResult updateMenu(Menu menu,ServerHttpRequest request) {
        Long loginUserId=this.getLoginUser(request).getUserId();
        this.menuService.updateMenu(menu,loginUserId);
        return ResResult.success();
    }

    //修改菜单排序值
    @RequestMapping("menu/menuSort")
    public ResResult menuSort(Long menuId,Long orderNum) {
        return menuService.menuSort(menuId,orderNum);
    }


    //删除菜单
    @RequestMapping("menu/delete")
    public ResResult deleteMenus(Long menuIds,ServerHttpRequest request) {
        Long loginUserId=this.getLoginUser(request).getUserId();
        this.menuService.deleteMeuns(menuIds,loginUserId);
        return ResResult.success();
    }

    //查询菜单树（包含按钮级别）
    @RequestMapping("notauth/menu/menuButtonTree")
    public ResResult getMenuButtonTree() {
        Tree<Menu> tree = this.menuService.getMenuButtonTree();
        return ResResult.success(tree);
    }

    //查询用户权限
    @RequestMapping("/notauth/menu/findUserPermissions")
    public ResResult findUserPermissions(ServerHttpRequest request) {
        User user=this.getLoginUser(request);
        if(user == null){
            return ResResult.error("登录超时!",ResResult.LOGIN_SESSION_TIMEOUT);
        }
        String username = user.getUsername();
        try{
            String userPermissions = redisService.get(RedisKeyContant.YOFISHDK_LOGIN_AUTH_PREFIX + username,String.class);
            List<Menu> permissionList = JSONObject.parseArray(userPermissions, Menu.class);
            return ResResult.success(permissionList);
        }catch (Exception ex){
            log.error("获取权限集发生异常:{}",ex);
            //直接从数据库获取一次
            List<Menu> permissionList = menuService.findUserPermissions(username);
            return ResResult.success(permissionList);
        }
    }

    //查询左侧菜单列表
    @RequestMapping("/notauth/menu/findLeftMenuList")
    public ResResult findLeftMenuList(ServerHttpRequest request) {
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
            tree.setRoleIds(menu.getRoleIds());
            trees.add(tree);
        }
        return ResResult.success(TreeUtils.build(trees));
    }
}
