package com.hjong.OnChat.service.impl;

import com.hjong.OnChat.entity.dto.ApiKey;
import com.hjong.OnChat.entity.enums.ServiceExceptionEnum;
import com.hjong.OnChat.exception.ServiceException;
import com.hjong.OnChat.repositories.ApiKeyRepositories;
import com.hjong.OnChat.service.ApiKeyService;
import com.hjong.OnChat.util.KeyUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/

@Service
public class ApiKeyServiceImpl implements ApiKeyService {

    @Resource
    ApiKeyRepositories apiKeyRepositories;
    @Resource
    KeyUtil keyUtil;

    @Override
    public Mono<Void> validateKey(String apiKey) {

        if (apiKey != null && apiKey.startsWith("Bearer ")) {
            apiKey = apiKey.substring(7);
        }

        return apiKeyRepositories.findByApiKey(apiKey)
                .hasElement()
                .flatMap(exists -> exists ? Mono.empty() : Mono.error(new ServiceException(ServiceExceptionEnum.INVALID_API_KEY)));
    }

    @Override
    public Mono<ApiKey> saveKey(String name,long expiresAt,Integer userId) {

        Instant instant = Instant.now();
        long timeStampSeconds = instant.getEpochSecond();

        ApiKey apiKey = new ApiKey()
                .setApiKey(keyUtil.generateKey(userId))
                .setCreatedAt(timeStampSeconds)
                .setExpiresAt(expiresAt)
                .setName(name)
                .setUserId(userId);

        return apiKeyRepositories.save(apiKey);
    }

    @Override
    public Mono<Void> deleteKey(Integer id) {
        return apiKeyRepositories.deleteById(id);
    }

    @Override
    public Mono<ApiKey> updateKey(ApiKey key) {
        return null;
    }

    @Override
    public Mono<List<ApiKey>> findByUserId(Integer userId) {

        return apiKeyRepositories.findByUserId(userId)
                .collectList();
    }
}
