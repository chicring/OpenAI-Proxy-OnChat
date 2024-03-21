package com.hjong.achat.repositories;

import com.hjong.achat.entity.DTO.ApiKey;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/

@Repository
public interface ApiKeyRepositories extends R2dbcRepository<ApiKey,Integer> {

    Mono<ApiKey> findByApiKey(String apiKey);
}
