package nirvana.cash.loan.privilege.domain;

import lombok.Data;
import nirvana.cash.loan.privilege.common.annotation.ExportConfig;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "tb_yofishdk_auth_role")
public class Role implements Serializable {

    /**
     * Role表序列
     */
/*
    public static final String SEQ = "seq_tb_yofishdk_auth_role";
*/

    @Id
    @Column(name = "ROLE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(name = "ROLE_NAME")
    @ExportConfig(value = "角色")
    private String roleName;

    @Column(name = "REMARK")
    @ExportConfig(value = "描述")
    private String remark;

    @Column(name = "CREATE_TIME")
    @ExportConfig(value = "创建时间", convert = "c:TimeConvert")
    private Date createTime;

    @Column(name = "MODIFY_TIME")
    private Date modifyTime;

    @ExportConfig(value = "角色编码")
    @Column(name = "role_code")
    private String roleCode;

    @Column(name = "ROLE_NAME2")
    @ExportConfig(value = "角色2")
    private String roleName2;

    @Transient
    String menuIds2;

}