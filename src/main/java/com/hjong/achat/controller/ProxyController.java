package com.hjong.achat.controller;

import com.hjong.achat.adapter.openai.OpenAiRequestBody;
import com.hjong.achat.service.ApiKeyService;
import com.hjong.achat.service.impl.ProxyServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/
@Slf4j
@RestController
public class ProxyController {

    @Resource
    ApiKeyService  apiKeyService;

    @Resource
    ProxyServiceImpl ProxyServiceImpl;

    @CrossOrigin
    @PostMapping(value = "/v1/chat/completions")
    public Publisher<String> Completions(@RequestBody OpenAiRequestBody requestBody, ServerWebExchange exchange) {

        ServerHttpResponse serverHttpResponse = exchange.getResponse();

        if(requestBody.isStream()){
            serverHttpResponse.getHeaders().setContentType(MediaType.TEXT_EVENT_STREAM);
        }else {
            serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        }

        return apiKeyService.validateKey(exchange.getRequest().getHeaders().getFirst("Authorization"))
                .thenMany(ProxyServiceImpl.completions(requestBody,exchange));
    }


    @GetMapping(value = "/test")
    public Mono<String> test(ServerWebExchange exchange) {
        log.info("来自ip：{} 的请求",exchange.getRequest().getRemoteAddress());
        return Mono.just("测试");
    }

}
