package nirvana.cash.loan.privilege.domain;

import lombok.Data;
import nirvana.cash.loan.privilege.common.annotation.ExportConfig;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "tb_yofishdk_auth_menu")
public class Menu implements Serializable {

    public static final String TYPE_MENU = "0";

    public static final String TYPE_BUTTON = "1";

	public static final String SEQ = "seq_tb_yofishdk_auth_menu";

    @Id
    @Column(name = "MENU_ID")
    @ExportConfig(value = "编号")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;

    @Column(name = "PARENT_ID")
    private Long parentId;

    @Column(name = "MENU_NAME")
    @ExportConfig(value = "名称")
    private String menuName;

    @Column(name = "URL")
    @ExportConfig(value = "地址")
    private String url;

    @Column(name = "PERMS")
    @ExportConfig(value = "权限标识")
    private String perms;

    @Column(name = "ICON")
    @ExportConfig(value = "图标")
    private String icon;

    @Column(name = "TYPE")
    @ExportConfig(value = "类型", convert = "s:0=菜单,1=按钮")
    private String type;

    @Column(name = "ORDER_NUM")
    private Long orderNum;

    @Column(name = "CREATE_TIME")
    @ExportConfig(value = "创建时间", convert = "c:TimeConvert")
    private Date createTime;

    @Column(name = "MODIFY_TIME")
    private Date modifyTime;

    @Transient
    private String role_ids;

    public String getType() {
        return StringUtils.isBlank(this.type) ? null : this.type.trim();
    }
}