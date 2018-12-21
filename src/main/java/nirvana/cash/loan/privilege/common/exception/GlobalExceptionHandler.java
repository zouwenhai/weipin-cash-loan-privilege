package nirvana.cash.loan.privilege.common.exception;

import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.contants.CommonContants;
import nirvana.cash.loan.privilege.common.util.ResResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String error = "系统内部异常";

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResResult jsonErrorHandler(Exception e) {
        logger.error("[exception]:message==>{} e==>{}", e.getMessage(), e);
        return ResResult.error(error, ResResult.ERROR);
    }

    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public ResResult jsonErrorHandler(ServerHttpRequest request, BizException e) {
        log.error("[exception]:traceId={},uri==>{},message==>{},e==>{}",
                request.getHeaders().getFirst(CommonContants.gateway_trace_id),request.getURI(), e.getMessage(), e);
        return ResResult.error(e.getDesc(), e.getCode());
    }

    @ExceptionHandler(value = LoginSesstionTimeOutException.class)
    @ResponseBody
    public ResResult jsonErrorHandler(ServerHttpRequest request, LoginSesstionTimeOutException e) {
        log.info("[exception]:traceId={},uri==>{},message==>{}",
                request.getHeaders().getFirst(CommonContants.gateway_trace_id), request.getURI(), e.getMessage());
        return ResResult.error(e.getDesc(), e.getCode());
    }


}
