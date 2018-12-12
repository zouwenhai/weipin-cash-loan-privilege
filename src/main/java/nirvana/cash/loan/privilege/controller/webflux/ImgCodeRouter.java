package nirvana.cash.loan.privilege.controller.webflux;

import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.contants.RedisKeyContant;
import nirvana.cash.loan.privilege.common.util.CodeImageUtil;
import nirvana.cash.loan.privilege.common.util.CookieUtil;
import nirvana.cash.loan.privilege.common.util.GeneratorId;
import nirvana.cash.loan.privilege.service.base.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Created by Administrator on 2018/11/1.
 */
@Slf4j
@Configuration
public class ImgCodeRouter {

    @Autowired
    private RedisService redisService;

    @Bean
    public RouterFunction<ServerResponse> gifCode() throws IOException {
        return route(GET("/privilige/notauth/gifCode").and(accept(APPLICATION_JSON)), request -> {
            //生成图形验证码
            CodeImageUtil codeImageUtil = new CodeImageUtil();
            byte[] imageBytes= codeImageUtil.getImageBytes();
            String verifyCode = codeImageUtil.getVerifyCode().toLowerCase();
            //图形验证码,,缓存5min
            String verifyId= GeneratorId.guuid();
            redisService.putWithExpireTime(verifyId,verifyCode,60 * 5L);
            //输出响应
            DataBuffer buffer = new DefaultDataBufferFactory().wrap(imageBytes);
            return ServerResponse
                            .ok()
                            .cookie(CookieUtil.buildCookie(RedisKeyContant.YOFISHDK_LOGIN_VERIFY_CODE,verifyId))
                            .contentType(MediaType.IMAGE_PNG)
                            .body(BodyInserters.fromDataBuffers(Flux.just(buffer)));
                }
        );
    }

}
