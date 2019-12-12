package nirvana.cash.loan.privilege.fegin.facade;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author zouwenhai
 * @version v1.0
 * @date 2019/12/10 16:02
 * @work //TODO
 */
@Getter
@Setter
public class OrderTopFacade implements Serializable {


    private static final long serialVersionUID = 6485894393366972463L;

    /**
     * 借款订单审核专员id
     */
    @NotNull(message = "id is not null")
    private Integer id;

    /**
     * 接单上限
     */
    private Integer orderTop;
}
