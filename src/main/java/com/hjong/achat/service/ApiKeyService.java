package com.hjong.achat.service;

import com.hjong.achat.entity.DTO.ApiKey;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/
public interface ApiKeyService {

    Mono<Void> validateKey(String key);

    Mono<ApiKey> saveKey(String name, long exp, Integer userId);

    Mono<Void> deleteKey(Integer id);

    Mono<ApiKey> updateKey(ApiKey key);

    Mono<List<ApiKey>> findByUserId(Integer userId);
}
