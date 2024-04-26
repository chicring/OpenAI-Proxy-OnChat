package com.hjong.OnChat.controller;

import com.hjong.OnChat.entity.dto.Channel;
import com.hjong.OnChat.entity.Result;
import com.hjong.OnChat.entity.dto.Model;
import com.hjong.OnChat.entity.vo.req.AddChannelVO;
import com.hjong.OnChat.entity.vo.req.UpdateChannelVO;
import com.hjong.OnChat.filter.annotation.CheckRole;
import com.hjong.OnChat.service.ChannelService;
import com.hjong.OnChat.service.ModelService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/27
 **/

@CheckRole
@Validated
@RestController
@RequestMapping("/channel")
public class ChannelController {

    @Resource
    ChannelService channelService;

    @Resource
    ModelService modelService;

    @GetMapping("/find")
    public Mono<Result<List<Channel>>> findAllChannel() {
        return channelService.findAll()
                .flatMap( list -> Mono.just(Result.ok(list)));
    }

    @PostMapping("/add")
    public Mono<Result<Void>> addChannel(@Valid @RequestBody AddChannelVO vo) {
        return channelService.saveChannel(vo).thenReturn(Result.ok("添加成功"));
    }

    @GetMapping("/delete")
    public Mono<Result<Void>> deleteChannel(@RequestParam Integer id) {
        return channelService.deleteChannel(id).thenReturn(Result.ok("删除成功"));
    }

    @PutMapping
    public Mono<Result<Void>> updateChannel(@RequestBody UpdateChannelVO vo) {
        return channelService.updateChannel(vo).thenReturn(Result.ok("更新成功"));
    }


    @GetMapping("/model/find")
    public Mono<Result<List<Model>>> findAllModel() {
        return modelService.findAll()
                .collectList()
                .flatMap( list -> Mono.just(Result.ok(list)));
    }

}
