package nirvana.cash.loan.privilege.domain;

import lombok.Data;

import java.util.List;

@Data
public class RoleWithMenu extends Role{

	private static final long serialVersionUID = 2013847071068967187L;
	
	private Long menuId;
	
	private List<Long> menuIds;

	private List<Long> buttonIds;

	private Integer menuType;

}
