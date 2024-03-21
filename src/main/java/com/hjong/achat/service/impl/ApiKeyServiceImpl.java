package com.hjong.achat.service.impl;

import com.hjong.achat.entity.DTO.ApiKey;
import com.hjong.achat.entity.Result;
import com.hjong.achat.enums.ServiceExceptionEnum;
import com.hjong.achat.exception.ServiceException;
import com.hjong.achat.repositories.ApiKeyRepositories;
import com.hjong.achat.service.ApiKeyService;
import com.hjong.achat.util.KeyUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.rmi.ServerException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
    public Mono<ApiKey> saveKey(String name,int expDay,Integer userId) {

        Instant instant = Instant.now();
        long timeStampSeconds = instant.getEpochSecond();

        Instant future = instant.plus(expDay, ChronoUnit.DAYS);
        long expiresStampSeconds = future.getEpochSecond();

        ApiKey apiKey = new ApiKey()
                .setApiKey(keyUtil.generateKey())
                .setCreatedAt(timeStampSeconds)
                .setExpiresAt(expiresStampSeconds)
                .setName(name)
                .setUserId(userId);

        return apiKeyRepositories.save(apiKey)
                .onErrorResume(e -> Mono.error(new ServiceException(ServiceExceptionEnum.SERVICE_EXCEPTION)));
    }

    @Override
    public Mono<Void> deleteKey(Integer id) {
        return apiKeyRepositories.deleteById(id)
                .onErrorResume(e -> Mono.error(new ServiceException(ServiceExceptionEnum.SERVICE_EXCEPTION)));
    }

    @Override
    public Mono<ApiKey> updateKey(ApiKey key) {
        return null;
    }
}
