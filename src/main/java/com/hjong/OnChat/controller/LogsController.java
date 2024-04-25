package com.hjong.OnChat.controller;

import com.hjong.OnChat.entity.dto.Logs;
import com.hjong.OnChat.entity.Result;
import com.hjong.OnChat.entity.vo.req.FindLogVO;
import com.hjong.OnChat.service.LogService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.hjong.OnChat.entity.Consts.*;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/24
 **/

@Validated
@RestController
@RequestMapping("/logs")
public class LogsController {
    @Resource
    LogService logService;

    @PostMapping("/find")
    public Mono<Result<List<Logs>>> findAll(@Valid @RequestBody(required = false) FindLogVO vo,
                                            @RequestHeader(USER_ID) Integer userId,
                                            @RequestHeader(ROLE) String role) {
        if (vo == null) {
            vo = new FindLogVO();
        }

        if (role.equals(USER_ROLE)) {
            vo.setUserId(userId);
        }
        return logService
                .findAll(vo)
                .collectList()
                .map(Result::ok);
    }



}
