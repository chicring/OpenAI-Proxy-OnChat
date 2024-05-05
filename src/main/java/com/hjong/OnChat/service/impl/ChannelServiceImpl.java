package com.hjong.OnChat.service.impl;


import com.hjong.OnChat.entity.dto.Channel;
import com.hjong.OnChat.entity.dto.Model;
import com.hjong.OnChat.entity.vo.req.ChannelVO;
import com.hjong.OnChat.entity.vo.req.UpdateChannelVO;
import com.hjong.OnChat.repositories.ChannelRepositories;
import com.hjong.OnChat.repositories.ModelRepository;
import com.hjong.OnChat.service.ChannelService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
    public Mono<Channel> saveChannel(ChannelVO vo) {
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
                    String[] models = c.getModels().split(",");

                    Integer channelId = c.getId();

                    List<Mono<Model>> monos = new ArrayList<>();
                    for (String m : models) {
                        Model model = new Model();
                        model.setRequestModel(m);
                        model.setRealModel(m);
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
    public Mono<Integer> updateChannel(ChannelVO vo) {

         return channelRepositories.updateById(vo.getName(), vo.getType(), vo.getApiKey(), vo.getBaseUrl(), vo.getModels(), vo.getPriority(), vo.isEnableProxy(), vo.getId())
                 .flatMap(c -> {
                     Integer channelId = vo.getId();

                     Mono<Void> deleteModelsMono = modelRepository.deleteByChannelId(channelId);

                     String[] models = vo.getModels().split(",");
                     List<Mono<Model>> addMonos = new ArrayList<>();
                     for (String m : models) {
                         Model model = new Model();
                         model.setRequestModel(m);
                         model.setRealModel(m);
                         model.setChannelId(channelId);
                         addMonos.add(modelRepository.save(model));
                     }
                     return deleteModelsMono.thenMany(Flux.concat(addMonos)).then(Mono.just(c));
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
