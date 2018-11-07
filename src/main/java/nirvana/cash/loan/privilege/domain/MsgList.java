package nirvana.cash.loan.privilege.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Administrator on 2018/11/5.
 */
@Data
@Table(name = "TB_YOFISHDK_AUTH_MSG_LIST")
public class MsgList {

    public static final String SEQ = "SEQ_TB_YOFISHDK_AUTH_MSG_LIST";

    //主键ID
    @Id
    @Column(name = "ID")
    private Long id;

    //用户ID
    @Column(name = "USER_ID")
    private Long userId;

    //消息唯一ID
    @Column(name = "UUID")
    private String uuid;

    //通知模块
    @Column(name = "MSG_MODULE")
    private Integer msgModule;

    //消息内容
    @Column(name = "CONTENT")
    private String content;

    //状态：0未读；1已读
    @Column(name = "STATUS")
    private Integer status;

    //创建时间
    @Column(name = "CREATE_TIME")
    private Date createTime;

    //更新时间
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    //创建者
    @Column(name = "CREATE_USER")
    private String createUser;

    //更新者
    @Column(name = "UPDATE_USER")
    private String updateUser;

    //是否删除
    @Column(name = "IS_DELETE")
    private Integer isDelete;

}
