package nirvana.cash.loan.privilege.domain.vo;

import lombok.Data;

import javax.persistence.Transient;

@Data
public class AuthDeptProductInfoVo {
    private Long deptId;
    private String deptName;
    private String productNos;
}
