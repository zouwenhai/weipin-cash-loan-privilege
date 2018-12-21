package nirvana.cash.loan.privilege.domain;

import lombok.Data;

import java.util.List;

@Data
public class UserWithRole extends User{
	
	private static final long serialVersionUID = -5680235862276163462L;
	
	private Long RoleId;
	
	private List<Long> roleIds;

}