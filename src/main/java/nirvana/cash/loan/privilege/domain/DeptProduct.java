package nirvana.cash.loan.privilege.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Table(name = "tb_yofishdk_auth_dept_prodt")
public class DeptProduct implements Serializable {

    public static final String SEQ = "seq_yofishdk_auth_dept_prodt";

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DEPT_ID")
    private Long deptId;

    @Column(name = "PRODUCT_NO")
    private String productNo;

}
