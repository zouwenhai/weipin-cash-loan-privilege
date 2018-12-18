package nirvana.cash.loan.privilege.mq.message;

import nirvana.cash.loan.privilege.common.enums.OrderStatusEnum;
import nirvana.cash.loan.privilege.service.MsgListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dongdong
 * @date 2018/12/18
 */
@Component
public class OldMessageProcessor {

    @Autowired
    private MsgListService msgListService;

    /**
     * ①机审失败模块的订单重新机审成功后（即工单状态变更为机审拒绝、人工复审、签约中或订单失效），通知状态变为已读
     * ②人工复审模块订单复审完成后（即工单状态变更为复审拒绝或签约中、订单失效），通知状态变为已读
     * ③放款失败模块的订单重新放款成功后（即工单状态变更为放款中、重新放款中、待还款、订单失效），通知状态变为已读
     * ④放款待审核模块的订单放款操作完成后（即工单状态变更为放款中、拒绝放款、订单失效），通知状态变为已读
     *
     * @param orderId
     * @param orderStatusEnum
     */
    public void markAsRead(String orderId, OrderStatusEnum orderStatusEnum) {
        switch (orderStatusEnum) {
            case SysRefused:
            case ManuaReview:
                //机审失败模块的订单重新机审成功后，通知状态变为已读
                msgListService.markAsRead(orderId, OrderStatusEnum.SysFailed.getValue());
                break;
            case OrderExpire:
                //机审失败模块的订单重新机审成功后，通知状态变为已读
                msgListService.markAsRead(orderId, OrderStatusEnum.SysFailed.getValue());
                //人工复审模块订单复审完成后，通知状态变为已读
                msgListService.markAsRead(orderId, OrderStatusEnum.ManuaReview.getValue());
                //放款失败模块的订单重新放款成功后，通知状态变为已读
                msgListService.markAsRead(orderId, OrderStatusEnum.LoanFailed.getValue());
                //放款待审核模块的订单放款操作完成后，通知状态变为已读
                msgListService.markAsRead(orderId, OrderStatusEnum.LoanApproving.getValue());
                break;
            case SignGoing:
                //机审失败模块的订单重新机审成功后，通知状态变为已读
                msgListService.markAsRead(orderId, OrderStatusEnum.SysFailed.getValue());
                //人工复审模块订单复审完成后，通知状态变为已读
                msgListService.markAsRead(orderId, OrderStatusEnum.ManuaReview.getValue());
                break;
            case ReviewRefused:
                //人工复审模块订单复审完成后，通知状态变为已读
                msgListService.markAsRead(orderId, OrderStatusEnum.ManuaReview.getValue());
                break;
            case ReLoan:
            case WaitRepay:
                //放款失败模块的订单重新放款成功后，通知状态变为已读
                msgListService.markAsRead(orderId, OrderStatusEnum.LoanFailed.getValue());
                break;
            case Loaning:
                //放款失败模块的订单重新放款成功后，通知状态变为已读
                msgListService.markAsRead(orderId, OrderStatusEnum.LoanFailed.getValue());
                //放款待审核模块的订单放款操作完成后，通知状态变为已读
                msgListService.markAsRead(orderId, OrderStatusEnum.LoanApproving.getValue());
                break;
            case LoanRefused:
                //放款待审核模块的订单放款操作完成后，通知状态变为已读
                msgListService.markAsRead(orderId, OrderStatusEnum.LoanApproving.getValue());
                break;
            default:
        }
    }

    /**
     * 如果同一订单重新机审仍然失败，不再重复发送通知，而是原通知保持未读
     *
     * @param orderId
     * @return
     */
    public boolean isSysFailedAgain(String orderId) {
        int count = msgListService.selectCountByOrderIdAndStatus(orderId, OrderStatusEnum.SysFailed.getDesc());
        return count > 0;
    }

}
