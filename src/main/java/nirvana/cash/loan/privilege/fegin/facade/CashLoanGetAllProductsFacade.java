package nirvana.cash.loan.privilege.fegin.facade;

import lombok.Data;

@Data
public class CashLoanGetAllProductsFacade {

    //产品名称
    private String name;

    //产品id(真实)
    private Long showId;

    //状态,0:下架,1:上架
    private Integer state;
}
