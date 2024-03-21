package com.hjong.achat.service.impl;

import com.hjong.achat.entity.DTO.Logs;
import com.hjong.achat.repositories.LogsRepositories;
import com.hjong.achat.service.LogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/19
 **/
@Slf4j
@Service
public class LogServiceImpl implements LogService {

    @Resource
    LogsRepositories logsRepositories;

    @Override
    public Mono<Logs> saveLog(Logs savelog) {

        return logsRepositories.save(savelog).onErrorResume(e -> {
            log.error("保存日志失败", e);
            return Mono.empty();
        });
    }

    @Override
    public Flux<Logs> getLogByChannelId(String channelId) {
        return null;
    }

    @Override
    public Flux<Logs> getLogByUserId(Integer userId) {
        return null;
    }

    @Override
    public Flux<Logs> getLogByUserName(String userName) {
        return null;
    }

    @Override
    public Flux<Logs> getLogByModel(String model) {
        return null;
    }

    @Override
    public Flux<Logs> getLogByChannelIdAndModel(String channelId, String model) {
        return null;
    }

    @Override
    public Flux<Logs> getLogByChannelIdAndUserId(String channelId, Integer userId) {
        return null;
    }

    @Override
    public Flux<Logs> getLogByChannelIdAndUserIdAndModel(String channelId, Integer userId, String model) {
        return null;
    }
}
