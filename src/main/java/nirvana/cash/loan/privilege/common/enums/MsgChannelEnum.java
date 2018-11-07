package nirvana.cash.loan.privilege.common.enums;

import lombok.Getter;

/**
 * Created by Administrator on 2018/11/7.
 */
@Getter
public enum MsgChannelEnum {

    channel_web_socket(1,"站内信"),
    channel_email(2,"邮件");

    MsgChannelEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private int value;
    private String desc;

    public static MsgChannelEnum getEnum(int code) {
        for (MsgChannelEnum  itemEnum : MsgChannelEnum.values()) {
            if (itemEnum.getValue() == code) {
                return itemEnum;
            }
        }
        return null;
    }
}
