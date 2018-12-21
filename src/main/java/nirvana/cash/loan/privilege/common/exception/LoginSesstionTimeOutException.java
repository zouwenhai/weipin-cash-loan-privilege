package nirvana.cash.loan.privilege.common.exception;

import lombok.Data;
import nirvana.cash.loan.privilege.common.util.ResResult;

/**
 * Created by Administrator on 2018/8/22.
 */
@Data
public class LoginSesstionTimeOutException extends RuntimeException {

    private String code;

    private String desc;

    public LoginSesstionTimeOutException() {
    }
    public LoginSesstionTimeOutException(String code, String desc) {
        super(desc);
        this.desc = desc;
        this.code = code;
    }

    public static LoginSesstionTimeOutException newInstance(String code, String desc){
        return new LoginSesstionTimeOutException(code,desc);
    }

    public static LoginSesstionTimeOutException newInstance(String desc) {
        return new LoginSesstionTimeOutException(ResResult.ERROR, desc);
    }

    public LoginSesstionTimeOutException(String message) {
        super(message);
    }

}

