package com.hjong.achat.service;

import com.hjong.achat.entity.DTO.Logs;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/19
 **/
public interface LogService {
    Mono<Logs> saveLog(Logs logs);
    Flux<Logs> getLogByChannelId(String channelId);
    Flux<Logs> getLogByUserId(Integer userId);
    Flux<Logs> getLogByUserName(String userName);
    Flux<Logs> getLogByModel(String model);
    Flux<Logs> getLogByChannelIdAndModel(String channelId, String model);
    Flux<Logs> getLogByChannelIdAndUserId(String channelId, Integer userId);
    Flux<Logs> getLogByChannelIdAndUserIdAndModel(String channelId, Integer userId, String model);

}
