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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.ArrayList;
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
            return menuMapper.findAllMenus(menu);
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
        return this.menuMapper.findLeftMenuList();
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


}
