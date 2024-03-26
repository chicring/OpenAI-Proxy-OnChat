package com.hjong.achat.service;

import com.hjong.achat.entity.DTO.Logs;
import com.hjong.achat.entity.VO.req.findLogVO;
import com.hjong.achat.entity.VO.resp.OverviewVO;
import com.hjong.achat.entity.VO.resp.RequestAmountVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/19
 **/
public interface LogService {
    Mono<Logs> saveLog(Logs logs);
    Flux<Logs> findAll(findLogVO vo);

    Mono<RequestAmountVO> logAmount();

    Mono<OverviewVO> overview();

}
