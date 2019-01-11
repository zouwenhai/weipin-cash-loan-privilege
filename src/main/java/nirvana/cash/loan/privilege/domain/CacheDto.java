package nirvana.cash.loan.privilege.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "TB_YOFISHDK_AUTH_CACHE")
public class CacheDto {

    public static final String SEQ = "SEQ_TB_YOFISHDK_AUTH_CACHE";

    @Column(name = "ID")
    private Long id;

    @Column(name = "KEY")
    private String key;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "remark")
    private String REMARK;

    @Column(name = "CREATE_TIME")
    private Date createTime;

}
