package nirvana.cash.loan.privilege.web.filter;

import com.alibaba.fastjson.JSONArray;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 网关代理,全局过滤器
 */
@Slf4j
@Order(200)
@Component
public class GatewayRequestGlobalFilter implements GlobalFilter{

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        URI uri = request.getURI();
        log.info("gateway request uri = {}", uri.toString());

        MultiValueMap<String, String> queryParams = request.getQueryParams();
        log.info("gateway queryParams = {}", JSONArray.toJSONString(queryParams));

        MediaType contentType = request.getHeaders().getContentType();
        if (contentType == null || MediaType.MULTIPART_FORM_DATA_VALUE.equals(contentType)) {
            return chain.filter(exchange);
        }

        HttpMethod httpMethod = request.getMethod();
        if (!httpMethod.matches("POST")) {
            return chain.filter(exchange);
        }

        Flux<DataBuffer> body = exchange.getRequest().getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(dataBuffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(dataBuffer.asByteBuffer());
            DataBufferUtils.release(dataBuffer);
            bodyRef.set(charBuffer.toString());
        });
        String requestBody = bodyRef.get();
        log.info("gateway request body:{}", requestBody);

        //request body只能读取一次,重新封装后传递给其他过滤器
        if (StringUtils.isNotBlank(requestBody)) {
            DataBuffer bodyDataBuffer = stringBuffer(requestBody);
            Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);
            request = new ServerHttpRequestDecorator(request) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return bodyFlux;
                }
            };
        }
        return chain.filter(exchange.mutate().request(request).build());
    }

    protected DataBuffer stringBuffer(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }
}
