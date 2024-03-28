package com.hjong.achat.controller;

import com.hjong.achat.entity.Result;
import com.hjong.achat.entity.VO.resp.StatusInfoVO;
import com.hjong.achat.service.ServerService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/28
 **/

@CrossOrigin
@RestController
public class ServerController {

    @Resource
    ServerService serverService;

    @GetMapping("/status")
    public Mono<Result<StatusInfoVO>> status() throws InterruptedException {

        return serverService.status()
                .flatMap( statusInfoVO -> {
                    Result<StatusInfoVO> result = Result.ok(statusInfoVO);
                    return Mono.just(result);
                });
    }
}
