package nirvana.cash.loan.privilege.fegin.facade;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author zouwenhai
 * @version v1.0
 * @date 2019/11/21 16:13
 * @work //TODO
 */
@Getter
@Setter
public class IsOpenSeatFacade implements Serializable {


    private static final long serialVersionUID = 3219260986709991506L;

    /*
     *借款订单审核专员id
     */
    private Integer Id;

    /**
     * 开启坐席（0：开启，1：关闭）
     */
    private Integer isOpenSeat;
}
