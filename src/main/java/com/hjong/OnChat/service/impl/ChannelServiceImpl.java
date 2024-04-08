package com.hjong.OnChat.service.impl;

import com.hjong.OnChat.entity.dto.Channel;
import com.hjong.OnChat.entity.vo.req.AddChannelVO;
import com.hjong.OnChat.entity.vo.req.UpdateChannelVO;
import com.hjong.OnChat.repositories.ChannelRepositories;
import com.hjong.OnChat.service.ChannelService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

import static com.hjong.OnChat.util.JsonUtil.parseJSONObject;
import static com.hjong.OnChat.util.JsonUtil.toJSONString;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/17
 **/

@Service
public class ChannelServiceImpl implements ChannelService {

    @Resource
    ChannelRepositories channelRepositories;

    @Override
    public Mono<Channel> saveChannel(AddChannelVO vo) {
        Channel channel = new Channel();
        channel.setName(vo.getName());
        channel.setType(vo.getType());
        channel.setModels(vo.getModels());
        channel.setApiKey(vo.getApiKey());
        channel.setPriority(vo.getPriority());
        channel.setBaseUrl(vo.getBaseUrl());
        channel.setEnableProxy(vo.isEnableProxy());
        channel.setCreatedAt(Instant.now().getEpochSecond());
        return channelRepositories.save(channel);
    }

    @Override
    public Mono<Void> deleteChannel(Integer id) {
        return channelRepositories.deleteById(id);
    }

    @Override
    public Mono<Channel> updateChannel(UpdateChannelVO vo) {
        Channel channel = new Channel();
        channel.setId(vo.getId());
        channel.setName(vo.getName());
        channel.setModels(vo.getModels());
        channel.setApiKey(vo.getApiKey());
        channel.setPriority(vo.getPriority());
        channel.setEnableProxy(vo.isEnableProxy());
        channel.setBaseUrl(vo.getBaseUrl());

        return channelRepositories.save(channel);
    }

    @Override
    public Mono<List<Channel>> selectChannel(String model) {
        return channelRepositories.selectChannel(model)
                .collectList();
    }

    @Override
    public Mono<List<Channel>> findAll(){
        return channelRepositories.findAll().collectList();
    }
}
