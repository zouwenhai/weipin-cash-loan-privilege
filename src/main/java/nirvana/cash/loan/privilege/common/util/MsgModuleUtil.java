package nirvana.cash.loan.privilege.common.util;

import nirvana.cash.loan.privilege.common.enums.MsgModuleEnum;
import nirvana.cash.loan.privilege.common.enums.OrderStatusEnum;

/**
 * Created by Administrator on 2018/11/8.
 */
public class MsgModuleUtil {

    public static MsgModuleEnum transOrderStatus2MsgModule(OrderStatusEnum orderStatusEnum) {
        MsgModuleEnum msgModuleEnum = null;
        if(orderStatusEnum == null){
            return msgModuleEnum;
        }
        switch (orderStatusEnum) {
            //机审失败
            case SysFailed:
                msgModuleEnum = MsgModuleEnum.MACHINE_FAIL;
                break;
            //人工复审审批中
            case ManuaReview:
                msgModuleEnum = MsgModuleEnum.MANUAL_REVIEW;
                break;
            //放款失败
            case LoanFailed:
                msgModuleEnum = MsgModuleEnum.LOAN_FAIL;
                break;
            //放款待审核
            case LoanApproving:
                msgModuleEnum = MsgModuleEnum.LOAN_CHECK_PENDING;
                break;
            default:
        }
        return msgModuleEnum;
    }

}
