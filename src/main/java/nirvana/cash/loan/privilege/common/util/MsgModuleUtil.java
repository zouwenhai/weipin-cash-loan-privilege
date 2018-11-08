package nirvana.cash.loan.privilege.common.util;

import nirvana.cash.loan.privilege.common.enums.MsgModuleEnum;
import nirvana.cash.loan.privilege.common.enums.OrderStatusEnum;

/**
 * Created by Administrator on 2018/11/8.
 */
public class MsgModuleUtil {

    public static MsgModuleEnum transOrderStatus2MsgModule(Integer orderStatus) {
        MsgModuleEnum msgModuleEnum = null;
        OrderStatusEnum orderStatusEnum = OrderStatusEnum.getEnum(orderStatus);
        if(orderStatusEnum == null){
            return msgModuleEnum;
        }
        switch (orderStatusEnum) {
            //机审失败
            case SysFailed:
                msgModuleEnum = MsgModuleEnum.MACHINE_FAIL;
            //人工复审审批中
            case ManuaReview:
                msgModuleEnum = MsgModuleEnum.MANUAL_REVIEW;
            //放款失败
            case LoanFailed:
                msgModuleEnum = MsgModuleEnum.LOAN_FAIL;
            //放款待审核
            case LoanApproving:
                msgModuleEnum = MsgModuleEnum.LOAN_CHECK_PENDING;
            //待催收
            case Penaltying:
                msgModuleEnum = MsgModuleEnum.CHECK_COLL;
            default:
        }
        return msgModuleEnum;
    }

}
