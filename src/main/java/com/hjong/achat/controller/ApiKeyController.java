package com.hjong.achat.controller;

import com.hjong.achat.entity.DTO.ApiKey;
import com.hjong.achat.entity.Result;
import com.hjong.achat.service.ApiKeyService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/17
 **/
@CrossOrigin
@RestController
@RequestMapping("/apikey")
public class ApiKeyController {

    @Resource
    ApiKeyService apiKeyService;

    @PostMapping("/save")
    public Mono<Result> saveApiKey() {

        Mono<ApiKey> apiKey = apiKeyService.saveKey("test", 1, 2);
        return apiKey.thenReturn(Result.ok());
    }

    @GetMapping("/find")
    public Mono<Result> findByUserId(ServerWebExchange exchange) {
        String userId = exchange.getRequest().getHeaders().getFirst("USER-ID");

        return apiKeyService.findByUserId(Integer.valueOf(userId)).collectList()
                .flatMap( list -> {
                    Result result = Result.ok(list);
                    return Mono.just(result);
                } );
    }
}
