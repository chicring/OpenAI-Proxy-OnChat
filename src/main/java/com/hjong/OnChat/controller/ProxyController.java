package com.hjong.OnChat.controller;

import com.hjong.OnChat.adapter.openai.OpenAiRequestBody;
import com.hjong.OnChat.service.impl.ProxyServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/

@RestController
public class ProxyController {

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

        return ProxyServiceImpl.completions(requestBody,exchange);
    }

}
