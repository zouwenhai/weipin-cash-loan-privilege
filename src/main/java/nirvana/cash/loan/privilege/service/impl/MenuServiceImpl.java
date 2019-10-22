package nirvana.cash.loan.privilege.service.impl;

import nirvana.cash.loan.privilege.common.domain.FilterId;
import nirvana.cash.loan.privilege.common.domain.Tree;
import nirvana.cash.loan.privilege.service.base.impl.BaseService;
import nirvana.cash.loan.privilege.common.util.ListUtil;
import nirvana.cash.loan.privilege.common.util.ResResult;
import nirvana.cash.loan.privilege.common.util.TreeUtils;
import nirvana.cash.loan.privilege.service.LogoutUserService;
import nirvana.cash.loan.privilege.service.MenuService;
import nirvana.cash.loan.privilege.service.RoleMenuServie;
import nirvana.cash.loan.privilege.dao.MenuMapper;
import nirvana.cash.loan.privilege.domain.Menu;
import nirvana.cash.loan.privilege.domain.vo.LeftMenuVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MenuServiceImpl extends BaseService<Menu> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RoleMenuServie roleMenuService;

    @Autowired
    private LogoutUserService logoutUserService;

    @Override
    public List<Menu> findUserPermissions(String userName) {
        return this.menuMapper.findUserPermissions(userName);
    }

    @Override
    public List<Menu> findAllMenus(Menu menu) {
        try {

            if (StringUtils.isEmpty(menu.getMenuName()) && StringUtils.isEmpty(menu.getType())) {
                List<Menu> rootMenu = menuMapper.selectAll();
                Example example = new Example(Menu.class);
                example.createCriteria().andEqualTo("parentId", 0L);
                example.orderBy("orderNum").asc();
                List<Menu> firstMenu = menuMapper.selectByExample(example);
                List<Menu> menuList = new ArrayList<Menu>();
                // 先找到所有的一级菜单
                firstMenu.forEach(menu1 -> {
                    menuList.add(menu1);
                    getChild1(menu1.getMenuId(), rootMenu, menuList);
                });
                return menuList;
            } else {//条件查询
                List<Menu> menuList = this.menuMapper.findAllMenus(menu);
                menuList.sort(new Comparator<Menu>() {
                    @Override
                    public int compare(Menu o1, Menu o2) {
                        return o1.getOrderNum().intValue() - o2.getOrderNum().intValue();
                    }
                });
                return this.menuMapper.findAllMenus(menu);
            }

        } catch (NumberFormatException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Tree<Menu> getMenuButtonTree() {
        List<Tree<Menu>> trees = new ArrayList<>();
        List<Menu> menus = this.findAllMenus(new Menu());
        buildTrees(trees, menus);
        return TreeUtils.build(trees);
    }

    @Override
    public Tree<Menu> getMenuTree() {
        List<Tree<Menu>> trees = new ArrayList<>();
        Example example = new Example(Menu.class);
        example.createCriteria().andCondition("type =", 0);
        example.setOrderByClause("create_time");
        List<Menu> menus = this.selectByExample(example);
        buildTrees(trees, menus);
        return TreeUtils.build(trees);
    }

    private void buildTrees(List<Tree<Menu>> trees, List<Menu> menus) {
        for (Menu menu : menus) {
            Tree<Menu> tree = new Tree<>();
            tree.setId(menu.getMenuId().toString());
            tree.setParentId(menu.getParentId().toString());
            tree.setText(menu.getMenuName());
            tree.setMenuType(menu.getType());
            trees.add(tree);
        }
    }

    @Override
    @Transactional
    public void addMenu(Menu menu) {
        /* menu.setMenuId(this.getSequence(Menu.SEQ));
         * 主键自增
         * */
        menu.setCreateTime(new Date());
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (menu.getOrderNum() == null) {
            menu.setOrderNum(1L);
        }
        this.save(menu);
    }

    @Override
    @Transactional
    public void deleteMeuns(Long menuId, Long loginUserId) {
        List<Menu> menus = this.findAllMenus(new Menu());
        if (menus != null && menus.size() > 0) {
            List<Long> userIdList = roleMenuService.findUserIdListByMenuId(menuId);
            if (userIdList != null && userIdList.size() > 0) {
                List<Long> newUserIdList = userIdList.stream().filter(t -> t.longValue() != loginUserId).collect(Collectors.toList());
                logoutUserService.batchLogoutUser(newUserIdList);
            }

            //转换列表
            List<FilterId> allList = new ArrayList<>();
            menus.forEach(t -> {
                FilterId filterId = new FilterId(t.getMenuId(), t.getParentId(), t.getMenuName());
                allList.add(filterId);
            });
            //开始处理...
            List<FilterId> filterIdList = FilterId.filterRemoveList(allList, menuId);
            List<String> list = new ArrayList<>();
            for (FilterId item : filterIdList) {
                list.add(item.getId() + "");
            }
            this.batchDelete(list, "menuId", Menu.class);
            this.roleMenuService.deleteRoleMenusByMenuId(list);
        }

        //process orderNum
        Menu menu = menus.stream()
                .filter(t -> t.getMenuId().longValue() == menuId)
                .findAny().orElse(null);
        Long parentId = menu.getParentId();
        this.resetOrderNum(parentId);
    }

    @Override
    public Menu findById(Long menuId) {
        return this.selectByKey(menuId);
    }

    @Override
    @Transactional
    public void updateMenu(Menu menu, Long loginUserId) {
        List<Long> userIdList = roleMenuService.findUserIdListByMenuId(menu.getMenuId());
        if (userIdList != null && userIdList.size() > 0) {
            List<Long> newUserIdList = userIdList.stream().filter(t -> t.longValue() != loginUserId).collect(Collectors.toList());
            logoutUserService.batchLogoutUser(newUserIdList);
        }

        Menu oldMenu = this.findById(menu.getMenuId());
        menu.setCreateTime(oldMenu.getCreateTime());
        menu.setModifyTime(new Date());
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        this.updateAll(menu);
    }

    @Override
    public List<LeftMenuVo> findUserMenus() {
        List<LeftMenuVo> rootMenu = menuMapper.findLeftMenuList();
        // 最后的结果
        List<LeftMenuVo> menuList = new ArrayList<LeftMenuVo>();
        // 先找到所有的一级菜单
        rootMenu.forEach(menuVo -> {
            menuList.add(menuVo);
            // 为一级菜单设置子菜单，getChild是递归调用的
            getChild2(menuVo.getMenuId(), rootMenu, menuList);
        });
        return menuList;
    }

    //按orderNum字段升序排序
    @Override
    public List<Menu> findByParentId(Long parentId) {
        //按orderNum字段升序排序
        Example example = new Example(Menu.class);
        Criteria criteria = example.createCriteria();
        criteria.andCondition("parent_id=", parentId);
        example.setOrderByClause("order_num asc ");
        return this.selectByExample(example);
    }

    @Override
    public void batchUpdateByIds(List<Menu> menuList) {
        for (Menu menu : menuList) {
            this.updateAll(menu);
        }
    }

    @Override
    public void resetOrderNum(Long parentId) {
        Date dt = new Date();
        List<Menu> parentMenuList = this.findByParentId(parentId);
        if (ListUtil.isNotEmpty(parentMenuList)) {
            for (int i = 0; i < parentMenuList.size(); i++) {
                Menu itemMenu = parentMenuList.get(i);
                itemMenu.setOrderNum((long) i + 1);
                itemMenu.setModifyTime(dt);
            }
            this.batchUpdateByIds(parentMenuList);
        }
    }

    @Override
    public ResResult menuSort(Long menuId, Long orderNum) {
        Menu menu = this.findById(menuId);
        if (menu.getOrderNum() == null) {
            menu.setOrderNum(1L);
        }
        menu.setOrderNum(orderNum);
        menu.setModifyTime(new Date());
        this.updateAll(menu);
        return ResResult.success();
    }


    private List<Menu> getChild1(Long id, List<Menu> rootMenu, List<Menu> menuList) {
        // 子菜单
        List<Menu> childList = new ArrayList<>();
        rootMenu.forEach(menu -> {
            if (menu.getParentId().equals(id)) {
                childList.add(menu);
            }
        });
        // 递归退出条件
        if (childList.size() == 0) {
            return null;
        }
        childList.sort(new Comparator<Menu>() {//按照orderNum升序排序
            @Override
            public int compare(Menu o1, Menu o2) {
                return o1.getOrderNum().intValue() - o2.getOrderNum().intValue();
            }
        });
        menuList.addAll(childList);
        // 把子菜单的子菜单再循环一遍
        childList.forEach(menu -> {
            // 递归
            getChild1(menu.getMenuId(), rootMenu, menuList);
        });
        return menuList;
    }


    private List<LeftMenuVo> getChild2(Long id, List<LeftMenuVo> rootMenu, List<LeftMenuVo> menuList) {
        // 子菜单
        List<LeftMenuVo> childList = new ArrayList<>();
        rootMenu.forEach(menuVo -> {
            // 遍历所有节点，将父菜单id与传过来的id比较
            if (menuVo.getParentId().equals(id)) {
                childList.add(menuVo);
            }
        });
        // 递归退出条件
        if (childList.size() == 0) {
            return null;
        }
        childList.sort(new Comparator<LeftMenuVo>() {//按照orderNum排序
            @Override
            public int compare(LeftMenuVo o1, LeftMenuVo o2) {
                return o1.getOrderNum().intValue() - o2.getOrderNum().intValue();
            }
        });
        menuList.addAll(childList);
        // 把子菜单的子菜单再循环一遍
        childList.forEach(menuVo -> {
            //递归
            getChild2(menuVo.getMenuId(), rootMenu, menuList);
        });
        return menuList;
    }

}
