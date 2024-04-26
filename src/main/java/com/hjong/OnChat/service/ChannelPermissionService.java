package com.hjong.OnChat.service;

import com.hjong.OnChat.entity.dto.ChannelPermission;
import com.hjong.OnChat.entity.vo.req.ChannelPermissionQueryVO;
import com.hjong.OnChat.entity.vo.resp.ChannelPermissionVO;
import com.hjong.OnChat.entity.vo.resp.PageVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChannelPermissionService {

    Mono<ChannelPermission> save(Integer userId, Integer channelId);

    Mono<Void> delete(Integer id);

    Mono<PageVO<ChannelPermissionVO>> find(ChannelPermissionQueryVO vo);

}
