package nirvana.cash.loan.privilege.domain;

import lombok.Data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "TB_YOFISHDK_AUTH_ROLE_MENU")
public class RoleMenu implements Serializable {
	
	private static final long serialVersionUID = -7573904024872252113L;

	@Column(name = "ROLE_ID")
    private Long roleId;

    @Column(name = "MENU_ID")
    private Long menuId;
}