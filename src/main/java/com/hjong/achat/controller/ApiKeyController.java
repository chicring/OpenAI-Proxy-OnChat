package com.hjong.achat.controller;

import com.hjong.achat.entity.DTO.ApiKey;
import com.hjong.achat.entity.Result;
import com.hjong.achat.entity.VO.req.SaveApikeyVO;
import com.hjong.achat.service.ApiKeyService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/17
 **/

@Validated
@CrossOrigin
@RestController
@RequestMapping("/apikey")
public class ApiKeyController {

    @Resource
    ApiKeyService apiKeyService;

    @PostMapping("/save")
    public Mono<Result<Void>> saveApiKey(@Valid @RequestBody SaveApikeyVO vo, ServerWebExchange exchange) {
        Integer userId = Integer.valueOf(Objects.requireNonNull(exchange.getRequest().getHeaders().getFirst("USER-ID")));

        Mono<ApiKey> apiKey = apiKeyService.saveKey(vo.getName(), vo.getExpiresAt(), userId);
        return apiKey.thenReturn(Result.ok());
    }

    @GetMapping("/find")
    public Mono<Result<List<ApiKey>>> findByUserId(ServerWebExchange exchange) {
        String userId = exchange.getRequest().getHeaders().getFirst("USER-ID");

        return apiKeyService.findByUserId(Integer.valueOf(userId))
                .flatMap( list -> {
                    return Mono.just(Result.ok(list));
                } );
    }
}
