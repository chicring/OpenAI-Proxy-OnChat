package com.hjong.OnChat.exception;

import com.hjong.OnChat.entity.Result;
import com.hjong.OnChat.util.JsonUtil;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/19
 **/
@Component
@Order(-1)
public class FilterExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);

        Result vo;
        if (ex instanceof ServiceException serviceException) {
            vo = Result.fail(serviceException.getServiceExceptionEnum());
        } else {
            vo = Result.fail(ex.getMessage());
        }

        DataBuffer buff = response.bufferFactory()
                .wrap(JsonUtil.toJSONString(vo).getBytes());
        //基于流形式
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeAndFlushWith(Mono.just(ByteBufMono.just(buff)));
    }
}
