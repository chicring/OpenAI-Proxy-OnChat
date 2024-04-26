
package com.hjong.OnChat.controller;

import com.hjong.OnChat.entity.Result;
import com.hjong.OnChat.entity.vo.req.ChannelPermissionQueryVO;
import com.hjong.OnChat.entity.vo.req.SavePermissionVO;
import com.hjong.OnChat.entity.vo.resp.ChannelPermissionVO;
import com.hjong.OnChat.entity.vo.resp.PageVO;
import com.hjong.OnChat.service.ChannelPermissionService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@Validated
@RestController
@RequestMapping("/permission")
public class ChannelPermissionController {

    @Resource
    ChannelPermissionService channelPermissionService;

    @PostMapping
    public Mono<Result<Void>> savePermission(@Valid @RequestBody SavePermissionVO vo) {
        return channelPermissionService.save(vo.getUserId(), vo.getChannelId()).thenReturn(Result.ok());
    }

    @DeleteMapping("/{id}")
    public Mono<Result<Void>> deletePermission(@PathVariable Integer id) {
        return channelPermissionService.delete(id).thenReturn(Result.ok());
    }

    @GetMapping
    public Mono<Result<PageVO<ChannelPermissionVO>>> findPermission(@Valid ChannelPermissionQueryVO vo) {
        return channelPermissionService.find(vo).map(Result::ok);
    }
}
