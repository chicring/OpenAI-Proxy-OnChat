package com.hjong.achat.controller;

import com.hjong.achat.entity.DTO.Logs;
import com.hjong.achat.entity.Result;
import com.hjong.achat.entity.VO.req.findLogVO;
import com.hjong.achat.entity.VO.resp.OverviewVO;
import com.hjong.achat.entity.VO.resp.RequestAmountVO;
import com.hjong.achat.service.LogService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/24
 **/

@Validated
@CrossOrigin
@RestController
@RequestMapping("/logs")
public class LogsController {
    @Resource
    LogService logService;

    @PostMapping("/find")

    public Mono<Result<List<Logs>>> findAll(@Valid @RequestBody(required = false) findLogVO vo) {
        if (vo == null) {
            findLogVO findLogVO = new findLogVO();
            return logService.findAll(findLogVO).collectList()
                    .flatMap( list -> {
                        Result<List<Logs>> result = Result.ok(list);
                        return Mono.just(result);
                    } );
        }
        return logService.findAll(vo).collectList()
                        .flatMap( list -> {
                            Result<List<Logs>> result = Result.ok(list);
                            return Mono.just(result);
                        } );
    }

    @GetMapping("/amount")
    public Mono<Result<RequestAmountVO>> findAmount() {
        return logService.logAmount()
                .flatMap( amount -> {
                    return Mono.just(Result.ok(amount));
                } );
    }

    @GetMapping("/overview")
    public Mono<Result<OverviewVO>> overview() {
        return logService.overview()
                .flatMap( overview -> {
                    return Mono.just(Result.ok(overview));
                } );
    }
}
