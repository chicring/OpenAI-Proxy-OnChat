package com.hjong.achat.service.impl;

import com.hjong.achat.adapter.Adapter;
import com.hjong.achat.adapter.openai.OpenAiRequestBody;
import com.hjong.achat.entity.DTO.Channel;
import com.hjong.achat.service.ChannelService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/

@Slf4j
@Service
public class ProxyServiceImpl {

    @Resource
    private Map<String, Adapter> selectorMap;

    @Resource
    ChannelService channelService;

    public Flux<String> completions(OpenAiRequestBody request){

        log.info("输入: {}", request.getMessages().getLast().getContent());

        if (request.getMessages().isEmpty()){
            return Flux.empty();
        }

        //Mono<List<Channel>>类型的，我想得到第一个的type
        //channelService.selectChannel(request.getModel())
        //然后执行
        //selectorMap.get(type).sendMessage(request, channel)

        return channelService.selectChannel(request.getModel())
                .flatMapMany(channels -> {
                    if (channels.isEmpty()) {
                        return Flux.empty();
                    }
                    Channel channel = channels.getFirst();
                    //根据type选择对应的Adapter
                    return selectorMap.get(channel.getType()).sendMessage(request, channel);
                });

    }
}
