package nirvana.cash.loan.privilege.common.enums;

import lombok.Data;

/**
 * Created by sunyong on 2018-11-07.
 * 消息模块枚举
 */
public enum MsgModuleEnum {

    // 1-机审失败
    MACHINE_FAIL(1, "机审失败"),
    // 2-人工复审审批中
    MANUAL_REVIEW(2, "人工复审审批中"),
    // 3-放款失败
    LOAN_FAIL(3, "放款失败"),
    // 4-放款待审核
    LOAN_CHECK_PENDING(4, "放款待审核"),
    // 5-待催收
    CHECK_COLL(5, "待催收");

    private int code;

    private String name;

    MsgModuleEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static MsgModuleEnum getMsgModuleEnum(int code) {
        for (MsgModuleEnum msgModuleEnum : MsgModuleEnum.values()) {
            if (msgModuleEnum.getCode() == code) {
                return msgModuleEnum;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
