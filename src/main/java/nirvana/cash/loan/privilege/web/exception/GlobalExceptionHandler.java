package nirvana.cash.loan.privilege.web.exception;

import nirvana.cash.loan.privilege.common.util.ResResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String error = "系统内部异常";

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResResult jsonErrorHandler(Exception e) {
        logger.error("[exception]:message==>{} e==>{}", e.getMessage(), e);
        return ResResult.error(error,ResResult.EXCEPTION);
    }


}
