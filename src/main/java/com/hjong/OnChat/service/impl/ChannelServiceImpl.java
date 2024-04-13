package com.hjong.OnChat.service.impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.hjong.OnChat.entity.dto.Channel;
import com.hjong.OnChat.entity.dto.Model;
import com.hjong.OnChat.entity.vo.req.AddChannelVO;
import com.hjong.OnChat.entity.vo.req.UpdateChannelVO;
import com.hjong.OnChat.repositories.ChannelRepositories;
import com.hjong.OnChat.repositories.ModelRepository;
import com.hjong.OnChat.service.ChannelService;
import com.hjong.OnChat.util.JsonUtil;
import jakarta.annotation.Resource;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/17
 **/

@Service
public class ChannelServiceImpl implements ChannelService {

    @Resource
    ChannelRepositories channelRepositories;

    @Resource
    ModelRepository modelRepository;

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
        return channelRepositories.save(channel)
                .flatMap(c -> {
                    Integer channelId = c.getId();
                    Map<String, String> map = JsonUtil.parseJSONArray(c.getModels(), new TypeReference<Map<String, String>>() {});
                    List<Mono<Model>> monos = new ArrayList<>();
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        Model model = new Model();
                        model.setRequestModel(entry.getKey());
                        model.setRealModel(entry.getValue());
                        model.setChannelId(channelId);
                        monos.add(modelRepository.save(model));
                    }
                    return Flux.concat(monos)
                            .then(Mono.just(c));
                });
    }

    @Override
    public Mono<Void> deleteChannel(Integer id) {

        Mono<Void> deleteChannelMono = channelRepositories.deleteById(id);

        Mono<Void> deleteModelsMono = deleteChannelMono
                .then(modelRepository.deleteByChannelId(id));

        return Mono.when(deleteChannelMono, deleteModelsMono)
                .then();
    }

    @Override
    public Mono<Channel> updateChannel(UpdateChannelVO vo) {
        return channelRepositories.findById(vo.getId())
                .flatMap(existingChannel -> {
                    existingChannel.setName(vo.getName());
                    existingChannel.setApiKey(vo.getApiKey());
                    existingChannel.setBaseUrl(vo.getBaseUrl());
                    existingChannel.setPriority(vo.getPriority());
                    existingChannel.setEnableProxy(vo.isEnableProxy());

                    // Handle added models
                    List<Mono<Model>> addMonos = new ArrayList<>();
                    for (String model : vo.getAdd()) {
                        Model newModel = new Model();
                        newModel.setRequestModel(model);
                        newModel.setRealModel(model);
                        newModel.setChannelId(existingChannel.getId());
                        addMonos.add(modelRepository.save(newModel));
                    }

                    // Handle removed models
                    List<Mono<Void>> removeMonos = new ArrayList<>();
                    for (String model : vo.getRemove()) {
                        removeMonos.add(modelRepository.deleteByRequestModelAndChannelId(model, existingChannel.getId()));
                    }

                    return Flux.concat(addMonos)
                            .thenMany(Flux.fromIterable(removeMonos))
                            .then(channelRepositories.save(existingChannel));
                });
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
