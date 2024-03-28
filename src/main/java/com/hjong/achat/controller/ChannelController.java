package com.hjong.achat.controller;

import com.hjong.achat.entity.DTO.Channel;
import com.hjong.achat.entity.Result;
import com.hjong.achat.service.ChannelService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/27
 **/
@CrossOrigin
@RestController
@RequestMapping("/channel")
public class ChannelController {

    @Resource
    ChannelService channelService;

    @GetMapping("/find")
    public Mono<Result<List<Channel>>> findAllChannel() {
        return channelService.findAll()
                .flatMap( list -> {
                    return Mono.just(Result.ok(list));
                } );
    }


}
