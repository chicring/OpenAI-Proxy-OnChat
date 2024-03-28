package com.hjong.achat.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hjong.achat.entity.DTO.Channel;
import com.hjong.achat.enums.ServiceExceptionEnum;
import com.hjong.achat.exception.ServiceException;
import com.hjong.achat.repositories.ChannelRepositories;
import com.hjong.achat.service.ChannelService;
import com.hjong.achat.util.JsonUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.hjong.achat.util.JsonUtil.parseJSONObject;
import static com.hjong.achat.util.JsonUtil.toJSONString;

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
    public Mono<Channel> saveChannel(Channel channel) {
        //添加生成时间
        channel.setCreatedAt(Instant.now().getEpochSecond());

        return channelRepositories.save(channel).onErrorResume(e -> Mono.error(new ServiceException(ServiceExceptionEnum.INVALID_ACTION)));
    }

    @Override
    public Mono<Void> deleteChannel(Integer id) {
        return null;
    }

    @Override
    public Mono<Channel> updateChannel(Channel channel) {
        return null;
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
