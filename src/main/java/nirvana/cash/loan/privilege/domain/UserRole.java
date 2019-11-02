package nirvana.cash.loan.privilege.domain;

import lombok.Data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "TB_YOFISHDK_AUTH_USER_ROLE")
public class UserRole implements Serializable{
    
	private static final long serialVersionUID = -3166012934498268403L;

	@Column(name = "USER_ID")
    private Long userId;

    @Column(name = "ROLE_ID")
    private Long roleId;
}