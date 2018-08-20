import com.alibaba.fastjson.JSON;
import nirvana.cash.loan.privilege.common.domain.FilterId;
import nirvana.cash.loan.privilege.common.util.MD5Utils;
import nirvana.cash.loan.privilege.system.domain.Menu;
import nirvana.cash.loan.privilege.system.domain.User;
import nirvana.cash.loan.privilege.system.service.MenuService;
import nirvana.cash.loan.privilege.system.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2018/8/6.
 */
public class LxwTest extends ApplicationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private MenuService menuService;

    @Test
    public void userAdd() {
        User user = new User();
        user.setUsername("liuxw");
        user.setPassword("123456");
        user.setUserId(userService.getSequence(User.SEQ));
        user.setCrateTime(new Date());
        user.setTheme(User.DEFAULT_THEME);
        user.setAvatar(User.DEFAULT_AVATAR);
        user.setPassword(MD5Utils.encrypt(user.getUsername(), user.getPassword()));
        user.setStatus("0");
        userService.save(user);
    }

    @Test
    public void updateUser() {
        User user = new User();
        user.setUserId(269L);
        user.setUsername("liuxiaowei");
        user.setDeptId(null);
        userService.updateUser(user, null);
    }

    @Test
    public void test2() {
        Integer menuId = 290;
        List<Menu> menus = menuService.findAllMenus(new Menu());
        if (menus != null && menus.size() > 0) {
            //转换列表
            List<FilterId> allList = new ArrayList<>();
            menus.forEach(t -> {
                FilterId filterId = new FilterId(t.getMenuId(), t.getParentId(), t.getMenuName());
                allList.add(filterId);
            });
            //开始处理...
            List<FilterId> filterIdList = FilterId.filterRemoveList(allList, menuId);
            System.err.println(JSON.toJSONString(filterIdList));
        }

    }

    @Test
    public void test() {
        Integer menuId = 266;
        Menu queryMenu = new Menu();
        List<Menu> menus = menuService.findAllMenus(queryMenu);
        //System.err.println(JSON.toJSONString(menus));
        List<Menu> resMenuList = findNeedRemoveMenuList(menus, menuId);
        System.err.println(JSON.toJSONString(resMenuList));

    }

    public List<Menu> findNeedRemoveMenuList(List<Menu> menus, long menuId) {
        List<Menu> resList = new ArrayList<>();
        filterById(resList, menus, menuId);
        filterSubMenuList(resList, menus, menuId);
        return resList;
    }


    public Menu filterById(List<Menu> resMenuList, List<Menu> menus, long menuId) {
        List<Menu> menuListList = menus.stream().filter(t -> t.getMenuId().longValue() == menuId).collect(Collectors.toList());
        resMenuList.add(menuListList.get(0));
        return menuListList.get(0);
    }

    public List<Menu> filterSubMenuList(List<Menu> resMenuList, List<Menu> menus, long menuId) {
        List<Menu> subMenuList = new ArrayList<>();
        for (Menu item : menus) {
            if (item.getParentId().longValue() == menuId) {
                subMenuList.add(item);
            }
        }
        if (subMenuList.size() > 0) {
            resMenuList.addAll(subMenuList);
            for (Menu subMenu : subMenuList) {
                filterSubMenuList(resMenuList, menus, subMenu.getMenuId());
            }
        }
        return subMenuList;
    }

}
