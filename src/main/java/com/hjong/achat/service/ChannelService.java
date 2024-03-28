package com.hjong.achat.service;

import com.hjong.achat.entity.DTO.Channel;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/17
 **/
public interface ChannelService {

    Mono<Channel> saveChannel(Channel channel);

    Mono<Void> deleteChannel(Integer id);

    Mono<Channel> updateChannel(Channel channel);

    Mono<List<Channel>> selectChannel(String model);

    Mono<List<Channel>> findAll();
}
