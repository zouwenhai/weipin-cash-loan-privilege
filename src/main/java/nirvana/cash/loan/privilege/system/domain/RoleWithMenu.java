package nirvana.cash.loan.privilege.system.domain;

import java.util.List;

public class RoleWithMenu extends Role{

	private static final long serialVersionUID = 2013847071068967187L;
	
	private Long menuId;
	
	private List<Long> menuIds;

	private List<Long> buttonIds;

	private Integer menuType;

	public Long getMenuId() {
		return menuId;
	}

	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}

	public List<Long> getMenuIds() {
		return menuIds;
	}

	public void setMenuIds(List<Long> menuIds) {
		this.menuIds = menuIds;
	}

	public Integer getMenuType() {
		return menuType;
	}

	public void setMenuType(Integer menuType) {
		this.menuType = menuType;
	}

	public List<Long> getButtonIds() {
		return buttonIds;
	}

	public void setButtonIds(List<Long> buttonIds) {
		this.buttonIds = buttonIds;
	}
}
