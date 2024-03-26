package com.hjong.achat.service.impl;

import com.hjong.achat.adapter.Adapter;
import com.hjong.achat.adapter.openai.OpenAiRequestBody;
import com.hjong.achat.entity.DTO.Channel;
import com.hjong.achat.enums.ServiceExceptionEnum;
import com.hjong.achat.exception.ServiceException;
import com.hjong.achat.repositories.ApiKeyRepositories;
import com.hjong.achat.service.ChannelService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
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

    @Resource
    ApiKeyRepositories apiKeyRepositories;

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

                    log.info("输入: {}", request.getMessages().getLast().getContent());

                    if (request.getMessages().isEmpty()){
                        return Flux.empty();
                    }

                    return channelService.selectChannel(request.getModel())
                            .flatMapMany(channels -> {
                                if (channels.isEmpty()) {
                                    return Mono.error(new ServiceException(ServiceExceptionEnum.CHANNEL_NOT_EXIST));
                                }
                                Channel channel = channels.getFirst();

                                return selectorMap.get(channel.getType()).sendMessage(request, channel,exchange);
                            })
                            .onErrorResume(e -> {
                                if (e instanceof ServiceException) {
                                    log.error("服务异常: {}", e.getMessage());
                                    return Mono.error(e);
                                }
                                return Mono.error(new ServiceException(ServiceExceptionEnum.SERVICE_EXCEPTION));
                            });
                });
    }
}
