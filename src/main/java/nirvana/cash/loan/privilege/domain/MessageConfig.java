package nirvana.cash.loan.privilege.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by sunyong on 2018-11-05.
 * 消息通知管理列表
 */
@Data
@Table(name = "TB_YOFISHDK_AUTH_MSG_CONFIG")
public class MessageConfig implements Serializable {

    private static final long serialVersionUID = -7865552752652420326L;

/*
    public static final String SEQ = "SEQ_TB_YOFISHDK_AUTH_MSG_CONFIG";
*/

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MSG_MODULE")
    private int msgModule;

    @Column(name = "IS_RUN")
    private int isRun;

    @Column(name = "CREATE_TIME")
    private Date createTime;

    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    @Column(name = "CREATE_USER")
    private String createUser;

    @Column(name = "UPDATE_USER")
    private String updateUser;

    @Column(name = "MSG_CONTENT")
    private String msgContent;
}
