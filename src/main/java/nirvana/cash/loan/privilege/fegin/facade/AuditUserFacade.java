package nirvana.cash.loan.privilege.fegin.facade;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author zouwenhai
 * @version v1.0
 * @date 2019/10/16 11:50
 * @work //借款审核人信息请求Facade
 */
@Setter
@Getter
public class AuditUserFacade implements Serializable {


    /**
     * 页码
     */
    private int pageNum;

    /**
     * 每页条数
     */
    private int pageSize;


}
