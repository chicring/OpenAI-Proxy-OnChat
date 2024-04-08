package com.hjong.OnChat.service.impl;

import com.hjong.OnChat.adapter.Adapter;
import com.hjong.OnChat.adapter.openai.OpenAiRequestBody;
import com.hjong.OnChat.entity.dto.Channel;
import com.hjong.OnChat.entity.enums.ServiceExceptionEnum;
import com.hjong.OnChat.exception.ServiceException;
import com.hjong.OnChat.repositories.ApiKeyRepositories;
import com.hjong.OnChat.service.ChannelService;
import com.hjong.OnChat.util.loadBalance.Strategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    @Value("${chat.channel.strategy}")
    private String channelStrategy;
    @Resource
    ChannelService channelService;

    @Resource
    ApiKeyRepositories apiKeyRepositories;
    @Resource
    private Map<String,Strategy> strategyMap;

    public Flux<String> completions(OpenAiRequestBody request, ServerWebExchange exchange) {

        String apiKey = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (apiKey != null && apiKey.startsWith("Bearer ")) {
            apiKey = apiKey.substring(7);
        }

        return apiKeyRepositories.findByApiKey(apiKey)
                .hasElement()
                .flatMapMany(exists -> {
                    if (!exists) {
                        return Mono.error(new ServiceException(ServiceExceptionEnum.INVALID_API_KEY));
                    }

//                    log.info("输入: {}", request.getMessages().getLast().getContent());

                    if (request.getMessages().isEmpty()){
                        return Flux.empty();
                    }

                    return channelService.selectChannel(request.getModel())
                            .flatMapMany(channels -> {
                                if (channels.isEmpty()) {
                                    log.error("渠道不存在");
                                    return Mono.error(new ServiceException(ServiceExceptionEnum.CHANNEL_NOT_EXIST));
                                }
                                Channel channel = strategyMap.get(channelStrategy).execute(channels);

                                return selectorMap.get(channel.getType()).sendMessage(request, channel,exchange);

                            });
                });
    }
}
