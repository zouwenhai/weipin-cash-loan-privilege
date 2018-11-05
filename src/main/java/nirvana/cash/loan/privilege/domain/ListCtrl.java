package nirvana.cash.loan.privilege.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Administrator on 2018/11/2.
 */
@Data
@Table(name = "TB_YOFISHDK_AUTH_LIST_CTRL")
public class ListCtrl {

    public static final String SEQ = "SEQ_TB_YOFISHDK_AUTH_LIST_CTRL";

    @Column(name = "ID")
    private Long id;

    @Column(name = "MENU_ID")
    private Long menuId;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "HIDDEN_COLUMN")
    private String hiddenColumn;

    @Column(name = "CREATE_TIME")
    private Date createTime;

    @Column(name = "UPDATE_TIME")
    private Date updateTime;

}
