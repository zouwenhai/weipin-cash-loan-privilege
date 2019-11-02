package nirvana.cash.loan.privilege.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import lombok.Data;
import nirvana.cash.loan.privilege.common.annotation.ExportConfig;

@Data
@Table(name = "TB_YOFISHDK_AUTH_USER")
public class User implements Serializable {

    private static final long serialVersionUID = -4852732617765810959L;

/*
    public static final String SEQ = "seq_tb_yofishdk_auth_user";
*/

    /**
     * 账户状态
     */
    public static final String STATUS_VALID = "1";

    public static final String STATUS_LOCK = "0";

    public static final String DEFAULT_THEME = "green";

    public static final String DEFAULT_AVATAR = "default.jpg";

    /**
     * 性别
     */
    public static final String SEX_MALE = "0";

    public static final String SEX_FEMALE = "1";

    public static final String SEX_UNKNOW = "2";


    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "JDBC")
    private Long userId;

    @Column(name = "USERNAME")
    @ExportConfig(value = "登录名")
    private String username;

    @Column(name = "NAME")
    @ExportConfig(value = "姓名")
    private String name;

    @Column(name = "PASSWORD")
    private String password;

    /**
     * 多个部门ID，使用逗号分隔
     */
    @Column(name = "DEPT_ID")
    private String deptId;

    @Transient
    @ExportConfig(value = "部门")
    private String deptName;

    @Column(name = "EMAIL")
    @ExportConfig(value = "邮箱")
    private String email;

    @Column(name = "MOBILE")
    @ExportConfig(value = "手机")
    private String mobile;

    @Column(name = "STATUS")
    @ExportConfig(value = "状态", convert = "s:0=锁定,1=有效")
    private String status;

    @Column(name = "CRATE_TIME")
    @ExportConfig(value = "创建时间", convert = "c:TimeConvert")
    private Date crateTime;

    @Column(name = "MODIFY_TIME")
    private Date modifyTime;

    @Column(name = "LAST_LOGIN_TIME")
    private Date lastLoginTime;

    @Column(name = "SSEX")
    @ExportConfig(value = "性别", convert = "s:0=男,1=女,2=保密")
    private String ssex;

    @Column(name = "THEME")
    private String theme;

    @Column(name = "AVATAR")
    private String avatar;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "is_delete")
    private Integer isDelete;

    @Transient
    private String roleName;

    @Transient
    private String roleIds2;

    @Column(name = "SEAT_NUMBER")
    private String seatNumber;

    @Column(name = "VIEW_RANGE")
    private Integer viewRange;


    @Column(name = "IS_SEPERATE")
    private Integer isSeperate;


}