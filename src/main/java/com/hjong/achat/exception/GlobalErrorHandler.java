package com.hjong.achat.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hjong.achat.entity.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/17
 **/
@Slf4j
@RestControllerAdvice
public class GlobalErrorHandler  {

//    @SneakyThrows
//    @Override
//    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
//
//        log.error("ex: ", ex);
//
//        ServerHttpResponse response = exchange.getResponse();
//        HttpHeaders headers = response.getHeaders();
//        headers.add("Content-Type", "application/json; charset=UTF-8");
//
//        if (ex instanceof ServiceException) {
//            byte[] bytes = new ObjectMapper().writeValueAsBytes(Result.fail(((ServiceException) ex).getServiceExceptionEnum()));
//
//            DataBuffer buffer = response.bufferFactory().wrap(bytes);
//            return response.writeWith(Mono.just(buffer));
//        }else {
//            // 其他异常
//            byte[] bytes = new ObjectMapper().writeValueAsBytes(Result.fail(ex.getMessage()));
//            DataBuffer buffer = response.bufferFactory().wrap(bytes);
//            return response.writeWith(Mono.just(buffer));
//        }
//
//    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<Result<Object>> handleException(ConstraintViolationException e){
        log.error("请求参数异常: {}",e.getMessage());
        return Mono.just(Result.fail(e.getMessage()));
    }

    @ExceptionHandler(value = ServiceException.class)
    public Mono<Result<Object>> handleServiceException(ServiceException e){
        log.error("业务异常：{}",e.getMessage());
        return Mono.just(Result.fail(e.getServiceExceptionEnum()));
    }

    @ExceptionHandler(value = WebClientResponseException.class)
    public Mono<Result<Object>> handleWebClientResponseException(WebClientResponseException e){
        HttpStatus status = (HttpStatus) e.getStatusCode();
        String res = e.getResponseBodyAsString();
        log.error("API error: {} {}", status, res);
        return Mono.just(Result.fail(res));
    }

    @ExceptionHandler(value = Exception.class)
    public Mono<Result<Object>> exceptionHandler(Exception e) {
        log.error("发生未知异常.",e);
        return Mono.just(Result.fail(e.getMessage()));
    }
}
