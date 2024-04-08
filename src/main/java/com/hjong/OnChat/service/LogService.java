package com.hjong.OnChat.service;

import com.hjong.OnChat.entity.dto.Logs;
import com.hjong.OnChat.entity.vo.req.FindLogVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/19
 **/
public interface LogService {
    Mono<Logs> saveLog(Logs logs);
    Flux<Logs> findAll(FindLogVO vo);
    Flux<Logs> findByUserId(FindLogVO vo);

}
