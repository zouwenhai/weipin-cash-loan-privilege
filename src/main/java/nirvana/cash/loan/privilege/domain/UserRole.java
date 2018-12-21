package nirvana.cash.loan.privilege.domain;

import lombok.Data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "tb_yofishdk_auth_user_role")
public class UserRole implements Serializable{
    
	private static final long serialVersionUID = -3166012934498268403L;

	@Column(name = "USER_ID")
    private Long userId;

    @Column(name = "ROLE_ID")
    private Long roleId;
}