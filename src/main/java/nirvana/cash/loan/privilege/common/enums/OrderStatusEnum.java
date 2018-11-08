package nirvana.cash.loan.privilege.common.enums;

import lombok.Getter;

/**
 * @author WangYx Date: 2018/8/1 Description: 订单状态
 */
@Getter
public enum OrderStatusEnum {

    OrderExpire(00, "订单失效"),
    SysReviewing(10, "机审中"),
    SysFailed(20, "机审失败"),
    SysRefused(30, "机审拒绝"),
    ManuaReview(40, "人工复审"),
    ReviewRefused(50, "复审拒绝"),
    SignGoing(60, "签约中"),
    SignFailed(70, "签约失败"),
    LoanApproving(80, "放款待审核"),
    LoanRefused(90, "拒绝放款"),
    Loaning(100, "放款中"),
    LoanFailed(110, "放款失败"),
    ReLoan(120, "重新放款中"),
    WaitRepay(130, "待还款"),
    Repaymenting(140, "还款中"),
    Penaltying(150, "已逾期"),
    Postponing(160, "宽限期中"),
    BadOrder(170, "坏账"),
    Withdrawing(180, "扣款中"),
    OverdueClosed(190, "逾期已结清"),
    ClosedOrder(200, "已结清"),
    AdvanceRepay(210, "提前结清");


    OrderStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private int value;
    private String desc;

    public static OrderStatusEnum getEnum(int value) {
        for (OrderStatusEnum itemEnum : OrderStatusEnum.values()) {
            if (itemEnum.getValue() == value) {
                return itemEnum;
            }
        }
        return null;
    }


}
