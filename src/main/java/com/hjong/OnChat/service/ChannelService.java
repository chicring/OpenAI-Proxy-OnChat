package com.hjong.OnChat.service;

import com.hjong.OnChat.entity.dto.Channel;
import com.hjong.OnChat.entity.vo.req.ChannelVO;
import com.hjong.OnChat.entity.vo.req.UpdateChannelVO;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/17
 **/
public interface ChannelService {

    Mono<Channel> saveChannel(ChannelVO vo);

    Mono<Void> deleteChannel(Integer id);

    Mono<Integer> updateChannel(ChannelVO vo);

    Mono<List<Channel>> selectChannel(String model);

    Mono<List<Channel>> findAll();
}
