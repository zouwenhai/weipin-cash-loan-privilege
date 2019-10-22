package nirvana.cash.loan.privilege.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Table(name = "TB_YOFISHDK_AUTH_CACHE")
public class CacheDto {

    /*  public static final String SEQ = "SEQ_TB_YOFISHDK_AUTH_CACHE";*/

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "JDBC")
    private Long id;

    @Column(name = "JSESSIONID")
    private String jsessionId;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "REMARK")
    private String REMARK;

    @Column(name = "CREATE_TIME")
    private Date createTime;

}
