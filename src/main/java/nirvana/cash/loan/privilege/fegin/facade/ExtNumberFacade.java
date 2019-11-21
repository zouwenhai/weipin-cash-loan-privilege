package nirvana.cash.loan.privilege.fegin.facade;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author zouwenhai
 * @version v1.0
 * @date 2019/11/21 16:59
 * @work //TODO
 */
@Getter
@Setter
public class ExtNumberFacade implements Serializable {


    private static final long serialVersionUID = 7759432440653450638L;

    /**
     * 借款订单审核专员id
     */
    @NotNull(message = "id is not null")
    private Integer id;

    /**
     * 分机号
     */
    private String extNumber;
}
