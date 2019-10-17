package nirvana.cash.loan.privilege.fegin.facade;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author zouwenhai
 * @version v1.0
 * @date 2019/10/16 11:07
 * @work //TODO
 */
@Getter
@Setter
public class IsDivideOrderFacade implements Serializable {


    /**
     * 营销专员id
     */
    private Long id;

    /**
     * 当前用户Id
     */
    private String userId;

    /**
     * 是否分单（0：不分单，1：分单）
     */
    private Integer isSeperate;
}
