package nirvana.cash.loan.privilege.web.exception;

import lombok.Data;
import nirvana.cash.loan.privilege.common.util.ResResult;

/**
 * Created by Administrator on 2018/8/22.
 */
@Data
public class BizException extends RuntimeException {

    private String code;

    private String desc;

    public BizException() {
    }
    public BizException(String code, String desc) {
        super(desc);
        this.desc = desc;
        this.code = code;
    }

    public static BizException newInstance(String code,String desc){
        return new BizException(code,desc);
    }

    public static BizException newInstance(String desc) {
        return new BizException(ResResult.ERROR, desc);
    }

    public BizException(String message) {
        super(message);
    }

}

