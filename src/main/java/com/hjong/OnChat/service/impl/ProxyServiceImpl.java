package com.hjong.OnChat.service.impl;

import com.hjong.OnChat.adapter.Adapter;
import com.hjong.OnChat.adapter.openai.OpenAiRequestBody;
import com.hjong.OnChat.chain.retrieve.Retrieve;
import com.hjong.OnChat.entity.dto.Channel;
import com.hjong.OnChat.entity.enums.ServiceExceptionEnum;
import com.hjong.OnChat.exception.ServiceException;
import com.hjong.OnChat.repositories.ApiKeyRepositories;
import com.hjong.OnChat.service.ChannelService;
import com.hjong.OnChat.util.loadBalance.Strategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hjong.OnChat.adapter.Consts.*;

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
    DatabaseClient databaseClient;

    @Resource
    private Map<String,Strategy> strategyMap;

    @Resource
    Retrieve retrieve;

    String key = "eyJhbGciOiJIUzI1NiIsInNpZ25fdHlwZSI6IlNJR04ifQ.eyJhcGlfa2V5IjoiMmM5YWZkNjY4OTMzZDVmOWQ4MmJjNzRhNDVhZmViZGUiLCJleHAiOjE4Njg1MTE3MDYwMDAsInRpbWVzdGFtcCI6MTcxMDc0NTM3NjAwMH0.jFlrKDJNfX6pNh1_nqa-wJZb0TUFyLhJy5I-VAwhlHI";

    public Flux<String> completions(OpenAiRequestBody request, ServerWebExchange exchange) {

        if (request.getMessages().isEmpty()){
            return Flux.empty();
        }

        AtomicBoolean enableWebSearch = new AtomicBoolean(false);

        if (request.getModel().startsWith(OPEN_WEB_SEARCH)){
            enableWebSearch.set(true);
            request.setModel(request.getModel().substring(4));
        }

        Integer userId = exchange.getAttribute("userId");

        List<String> question = List.of(request.getMessages().getLast().getContent());

        return this.selectChannel(request.getModel(), userId)
                .flatMap(channels -> {
                    if (channels.isEmpty()) {
                        log.warn("渠道不存在");
                        return Mono.error(new ServiceException(ServiceExceptionEnum.CHANNEL_NOT_EXIST));
                    }
                    Channel channel = strategyMap.get(channelStrategy).execute(channels);

                    if (enableWebSearch.get()){
                        channel.setModel(OPEN_WEB_SEARCH + channel.getModel());
                    }

                    return retrieve.retrieveknowledge(question, "tt", key)
                            .doOnNext(answer -> request.getMessages().getLast().setContent(answer))
                            .doOnNext(answer -> log.debug("prompt：{}", answer))
                            .thenReturn(channel);

                })
                .flatMapMany(channel -> selectorMap.get(channel.getType()).sendMessage(request, channel,exchange));
    }

    private Mono<List<Channel>> selectChannel(String requestModel, Integer userId){
        String sql = "SELECT c.id, c.name, c.type, c.api_key, c.base_url, c.priority, c.enable_proxy, m.real_model " +
                "FROM Channel c " +
                "JOIN Model m ON c.id = m.channel_id " +
                "LEFT JOIN ChannelPermission p ON c.id = p.channel_id AND p.user_id = :userId " +
                "WHERE m.request_model = :requestModel " +
                "AND p.id IS NULL;";


        return databaseClient.sql(sql)
                .bind("requestModel", requestModel)
                .bind("userId", userId)
                .map((row, metadata) -> {
                    Channel channel = new Channel();
                    channel.setId(row.get("id", Integer.class));
                    channel.setName(row.get("name", String.class));
                    channel.setType(row.get("type", String.class));
                    channel.setApiKey(row.get("api_key", String.class));
                    channel.setBaseUrl(row.get("base_url", String.class));
                    channel.setPriority(row.get("priority", Integer.class));
                    channel.setEnableProxy(Boolean.TRUE.equals(row.get("enable_proxy", Boolean.class)));
                    channel.setModel(row.get("real_model", String.class));
                    return channel;
                }).all().collectList();

    }
}
