package com.hjong.achat.controller;

import com.hjong.achat.entity.DTO.ApiKey;
import com.hjong.achat.entity.Result;
import com.hjong.achat.service.ApiKeyService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/17
 **/

@RestController
public class ApiKeyController {

    @Resource
    ApiKeyService apiKeyService;

    @PostMapping("/save")
    public Mono<Result> saveApiKey() {

        Mono<ApiKey> apiKey = apiKeyService.saveKey("test", 1, 2);
        return apiKey.thenReturn(Result.ok());
    }
}
