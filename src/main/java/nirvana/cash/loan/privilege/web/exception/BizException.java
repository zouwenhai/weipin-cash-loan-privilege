package nirvana.cash.loan.privilege.web.exception;

/**
 * Created by Administrator on 2018/8/22.
 */
public class BizException extends RuntimeException {

    public BizException() {
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }
}

