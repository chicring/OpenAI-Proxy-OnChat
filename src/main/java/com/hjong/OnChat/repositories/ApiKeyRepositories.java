package com.hjong.OnChat.repositories;

import com.hjong.OnChat.entity.dto.ApiKey;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/

@Repository
public interface ApiKeyRepositories extends R2dbcRepository<ApiKey,Integer> {

    Mono<ApiKey> findByApiKey(String apiKey);

    Flux<ApiKey> findByUserId(Integer userId);
}
