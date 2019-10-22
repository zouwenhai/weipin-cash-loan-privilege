package nirvana.cash.loan.privilege.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Table(name = "TB_YOFISHDK_AUTH_DEPT_PRODT")
public class DeptProduct implements Serializable {

    public static final String SEQ = "seq_yofishdk_auth_dept_prodt";

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.TABLE,generator = "JDBC")
    private Long id;

    @Column(name = "DEPT_ID")
    private Long deptId;

    @Column(name = "PRODUCT_NO")
    private String productNo;

}
