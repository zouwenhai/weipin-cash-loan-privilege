package mysql;

import nirvana.cash.loan.privilege.Application;
import nirvana.cash.loan.privilege.dao.CacheMapper;
import nirvana.cash.loan.privilege.dao.DeptMapper;
import nirvana.cash.loan.privilege.dao.MenuMapper;
import nirvana.cash.loan.privilege.domain.CacheDto;
import nirvana.cash.loan.privilege.domain.Dept;
import nirvana.cash.loan.privilege.domain.Menu;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.service.DeptProductService;
import nirvana.cash.loan.privilege.service.DeptService;
import nirvana.cash.loan.privilege.service.MenuService;
import nirvana.cash.loan.privilege.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author zouwenhai
 * @version v1.0
 * @date 2019/10/21 10:52
 * @work //TODO
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class DeptControllerTest {

    @Autowired
    private DeptService deptService;
    @Autowired
    private DeptProductService deptProductService;

    @Resource
    private MenuMapper menuMapper;

    @Resource
    private MenuService menuService;

    @Resource
    private CacheMapper cacheMapper;

    @Resource
    private DeptMapper deptMapper;

    @Autowired
    private UserService userService;

    @Test
    public void testDeptList2() {

       CacheDto dto = new CacheDto();
        dto.setId(9214L);
        dto.setJsessionId("123123132132");
        dto.setValue("1231231");
        dto.setREMARK("sdfsdfsfsfsd");
        cacheMapper.insertSelective(dto);
 /*       Dept dept = new Dept();
        dept.setDeptId(221L);
        List<Dept> list = this.deptService.findAllDepts(dept);
        list.forEach(t -> {
            if (t.getViewRange() == 1) {
                String productNos = deptProductService.findProductNosByDeptId(t.getDeptId());
                t.setProductNos(productNos);
            }
        });*/
    }

    @Test
    public void testDeptList() {

        Dept dept = new Dept();
        dept.setViewRange(1);
        dept.setParentId(0L);
        dept.setDeptName("主渠道");
        deptMapper.insertSelective(dept);
    }

    public List<Menu> getTreeList(String topId) {
        Menu menu = new Menu();
        menu.setParentId(0L);
        List<Menu> firstmenus = menuMapper.selectByExample(menu);
        List<Menu> trees = getSubList(firstmenus);

        return trees;
    }

    private List<Menu> getSubList(List<Menu> entityList) {
        for (int i = 0; i < entityList.size(); i++) {
            Menu menu = new Menu();
            menu.setParentId(entityList.get(i).getMenuId());
            List<Menu> firstmenus = menuMapper.selectByExample(menu);
            if (firstmenus != null && firstmenus.size() > 0) {

            }
        }
        ;

        return null;
    }

    /**
     * findAllMenus
     */
    @Test
    public void testQueryMenuList() {
        // 原始的数据
        List<Menu> rootMenu = menuMapper.selectAll();
        Example example = new Example(Menu.class);
        example.createCriteria().andEqualTo("parentId", 0L);
        example.orderBy("orderNum").asc();
        List<Menu> firstMenu = menuMapper.selectByExample(example);
      /*  // 查看结果
        for (Menu menu : rootMenu) {
            System.out.println(menu);
        }*/
        // 最后的结果
        List<Menu> menuList = new ArrayList<Menu>();
        // 先找到所有的一级菜单
        for (int i = 0; i < firstMenu.size(); i++) {
            // 一级菜单没有parentId
            menuList.add(firstMenu.get(i));
            // 为一级菜单设置子菜单，getChild是递归调用的
            getChild(firstMenu.get(i).getMenuId(), rootMenu, menuList);
        }
        System.out.println(menuList);

    }

    /**
     * 递归查找子菜单
     *
     * @param id       当前菜单id
     * @param rootMenu 要查找的列表
     * @return
     */
    private List<Menu> getChild(Long id, List<Menu> rootMenu, List<Menu> menuList) {
        // 子菜单
        List<Menu> childList = new ArrayList<>();
        for (Menu menu : rootMenu) {
            // 遍历所有节点，将父菜单id与传过来的id比较
            if (menu.getParentId() != null) {
                if (menu.getParentId().equals(id)) {
                    childList.add(menu);
                    childList.sort(new Comparator<Menu>() {//按照orderNum排序
                        @Override
                        public int compare(Menu o1, Menu o2) {
                            return o1.getOrderNum().intValue() - o2.getOrderNum().intValue();
                        }
                    });
                }
            }
        }
        // 递归退出条件
        if (childList.size() == 0) {
            return null;
        }
        menuList.addAll(childList);
        // 把子菜单的子菜单再循环一遍
        for (Menu menu : childList) {// 没有url子菜单还有子菜单
            // 递归
            getChild(menu.getMenuId(), rootMenu, menuList);
        }


        return menuList;
    }


    @Test
    public void addList() {

        Menu menu = new Menu();
        menu.setParentId(0L);
        menu.setMenuId(1L);
        Menu menu1 = new Menu();
        menu1.setMenuId(2L);
        menu1.setParentId(0L);
        List<Menu> result = new ArrayList<>();

        List<Menu> list1 = new ArrayList<>();
        list1.add(menu);
        list1.add(menu1);
        List<Menu> list2 = new ArrayList<>();
        list2.add(menu);
        list2.add(menu1);
        result.addAll(list1);
        result.addAll(list2);
    }

    @Test
    public void test3(){
      User user =  userService.findByName("system");
    }

}