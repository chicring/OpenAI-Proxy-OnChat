package com.hjong.OnChat.repositories;

import com.hjong.OnChat.entity.dto.Model;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/13
 **/
@Repository
public interface ModelRepository extends R2dbcRepository<Model,Integer> {

    Mono<Void> deleteByChannelId(Integer channelId);


    Mono<Void> deleteByRequestModelAndChannelId(String requestModel, Integer channelId);
}
