package nirvana.cash.loan.privilege.common.domain;

import nirvana.cash.loan.privilege.domain.Menu;
import nirvana.cash.loan.privilege.domain.RoleWithMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/15.
 */
public class SplitMenu {

    private List<RoleWithMenu> leafList;

    private List<RoleWithMenu> parentList;

    public void splitMenuList(List<Menu> allList, List<RoleWithMenu> roleWithMenuList) {
        leafList = new ArrayList<>();
        parentList = new ArrayList<>();
        roleWithMenuList.forEach(t -> {
            if (this.isLeafMenu(allList, t.getMenuId())) {
                leafList.add(t);
            } else {
                parentList.add(t);
            }
        });
    }

    private boolean isLeafMenu(List<Menu> allList, long menuId) {
        boolean isLeaf = true;
        for (Menu item : allList) {
            if (item.getParentId() == menuId) {
                isLeaf = false;
                break;
            }
        }
        return isLeaf;
    }

    public List<RoleWithMenu> getLeafList() {
        return leafList;
    }

    public void setLeafList(List<RoleWithMenu> leafList) {
        this.leafList = leafList;
    }

    public List<RoleWithMenu> getParentList() {
        return parentList;
    }

    public void setParentList(List<RoleWithMenu> parentList) {
        this.parentList = parentList;
    }
}
