package com.hjong.OnChat.exception;

import com.hjong.OnChat.entity.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.MissingRequestValueException;
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

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<Result<Object>> handleException(WebExchangeBindException e){
        log.error("参数校验异常: {}",e.getFieldError().getDefaultMessage());
        return Mono.just(Result.fail(e.getFieldError().getDefaultMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<Result<Object>> handleException(ConstraintViolationException e){
        log.error("请求参数异常: {}",e.getMessage());
        return Mono.just(Result.fail(e.getMessage()));
    }

    @ExceptionHandler(value = ServiceException.class)
    public Mono<Result<Object>> handleServiceException(ServiceException e){
        log.error("自定义异常：{}",e.getMessage());
        return Mono.just(Result.fail(e.getServiceExceptionEnum()));
    }

    @ExceptionHandler(value = WebClientRequestException.class)
    public Mono<Result<Object>> handleWebClientRequestException(WebClientRequestException e){
        log.error("请求异常: {}", e.getMessage());
        return Mono.just(Result.fail(e.getMessage()));
    }

    @ExceptionHandler(value = WebClientResponseException.class)
    public Mono<Result<Object>> handleWebClientResponseException(WebClientResponseException e){
        HttpStatus status = (HttpStatus) e.getStatusCode();
        String res = e.getResponseBodyAsString();
        log.error("API error: {} {}", status, res);
        return Mono.just(Result.fail(res));
    }

    @ExceptionHandler(value = MissingRequestValueException.class)
    public Mono<Result<Object>> handleMissingRequestValueException(MissingRequestValueException e){
        log.error(e.getMessage());
        return Mono.just(Result.fail("缺少请求参数"));
    }

    @ExceptionHandler(value = Exception.class)
    public Mono<Result<Object>> exceptionHandler(Exception e) {
        log.error("异常类型 {}",e.getClass());
        log.error("异常信息 {}",e.getMessage());
        log.error("异常信息 {}", (Object) e.getStackTrace());
        return Mono.just(Result.fail(e.getMessage()));
    }
}
