package com.hjong.OnChat.controller;

import com.hjong.OnChat.entity.dto.ApiKey;
import com.hjong.OnChat.entity.Result;
import com.hjong.OnChat.entity.vo.req.SaveApikeyVO;
import com.hjong.OnChat.service.ApiKeyService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.hjong.OnChat.entity.Consts.USER_ID;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/17
 **/

@Validated
@RestController
@RequestMapping("/apikey")
public class ApiKeyController {

    @Resource
    ApiKeyService apiKeyService;

    @PostMapping("/create")
    public Mono<Result<Void>> saveApiKey(@Valid @RequestBody SaveApikeyVO vo, @RequestHeader(USER_ID) Integer userId){

        Mono<ApiKey> apiKey = apiKeyService.saveKey(vo.getName(), vo.getExpiresAt(), userId);
        return apiKey.thenReturn(Result.ok());
    }

    @GetMapping("/find")
    public Mono<Result<List<ApiKey>>> findByUserId(@RequestHeader(USER_ID) Integer userId) {

        return apiKeyService.findByUserId(userId)
                .flatMap( list -> {
                    return Mono.just(Result.ok(list));
                } );
    }

    @GetMapping("/delete")
    public Mono<Result<Void>> deleteApiKey(@RequestParam Integer id) {
        return apiKeyService.deleteKey(id).thenReturn(Result.ok());
    }
}
