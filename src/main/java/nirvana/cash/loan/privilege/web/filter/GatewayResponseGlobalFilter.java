package nirvana.cash.loan.privilege.web.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nirvana.cash.loan.privilege.common.util.ByteUtil;
import nirvana.cash.loan.privilege.domain.ListCtrl;
import nirvana.cash.loan.privilege.domain.User;
import nirvana.cash.loan.privilege.service.ListCtrlService;
import nirvana.cash.loan.privilege.web.RequestCheck;
import org.apache.commons.lang.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * 网关代理,全局过滤器
 */
@Slf4j
@Order(-200)  //注意order要小于-1.通过上面的类，就能查看服务端响应的值了
@Component
public class GatewayResponseGlobalFilter implements GlobalFilter {

    @Autowired
    private RequestCheck requestCheck;

    private final static String paramMenuId = "menuId";

    @Autowired
    private ListCtrlService listCtrlService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest originalRequest = exchange.getRequest();
        ListCtrl listCtrl = getListCtrl(originalRequest);
        if (listCtrl == null || StringUtils.isBlank(listCtrl.getHiddenColumn())) {
            return chain.filter(exchange);
        }
        String hiddenColumn = listCtrl.getHiddenColumn();
        //修改返回参数|返回参数添加:用户配置的列表隐藏列字段
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.map(dataBuffer -> {
                        // probably should reuse buffers
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(content);
                        //释放掉内存
                        DataBufferUtils.release(dataBuffer);
                        //res就是response的值，想修改、查看就随意而为了
                        String res = new String(content, Charset.forName("UTF-8"));
                        log.info("gateway response body:{}",res);
                        //返回值|添加隐藏列字段
                        JSONObject resjson = JSONObject.parseObject(res);
                        resjson.put("hiddenColumn",hiddenColumn);
                        //更新返回数据
                        byte[] uppedContent = ByteUtil.json2Bytes(resjson,"utf-8");
                        return bufferFactory.wrap(uppedContent);
                    }));
                }
                // if body is not a flux. never got there.
                return super.writeWith(body);
            }
        };
        // replace response with decorator
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    protected ListCtrl getListCtrl(ServerHttpRequest request){
        //查询登录用户信息，未查询到,不处理
        User user = requestCheck.getLoginUser(request);
        if (user == null) {
            return null;
        }

        //查询字符串为空 或者 菜单ID,不处理
        MultiValueMap<String, String> queryParam = request.getQueryParams();
        log.info("queryParam:{}", queryParam != null?JSON.toJSONString(queryParam):null);

        if(queryParam == null || StringUtils.isBlank(queryParam.getFirst(paramMenuId))){
            return null;
        }

        //获取菜单ID值,若未获取到,不处理
        String menuIdStr = queryParam.getFirst(paramMenuId);

        //query user list hiddenColumn
        Long userId = user.getUserId();
        Long menuId = Long.valueOf(menuIdStr);
        log.info("query list hiddenColumn:userId={},menuId={}", userId, menuId);
        ListCtrl listCtrl = listCtrlService.findListCtrl(userId, menuId);
        if (listCtrl == null || StringUtils.isBlank(listCtrl.getHiddenColumn())) {
            return null;
        }
        return listCtrl;
    }
}
