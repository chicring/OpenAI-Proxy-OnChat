package com.hjong.OnChat.adapter;

import com.hjong.OnChat.adapter.openai.OpenAiRequestBody;
import com.hjong.OnChat.entity.dto.Channel;
import com.hjong.OnChat.entity.enums.ChannelType;
import com.hjong.OnChat.entity.enums.ChatRoleEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.hjong.OnChat.entity.enums.UrlEnums.getUrlByType;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/

@Slf4j
public abstract class Adapter {

    @Resource
    private WebClient webClient;

    @Resource
    private WebClient webClientEnableProxy;


    protected WebClient getWebClient(Boolean enableProxy) {
        return enableProxy ? webClientEnableProxy : webClient;
    }

    protected String buildEndpoint(String baseurl, String channelType){

        return baseurl + getUrlByType(channelType);
    }

    protected abstract Flux<String> completions(OpenAiRequestBody request,String baseurl, String apikey, Boolean enableProxy);

    protected abstract Flux<String> streamCompletions(OpenAiRequestBody request,String baseurl, String apikey, Boolean enableProxy);

    public Flux<String> sendMessage(OpenAiRequestBody request, Channel channel, ServerWebExchange serverWebExchange){


        validateAndSetParameters(request);

        log.debug("请求类型：{},请求站点：{}",channel.getType(),channel.getBaseUrl());

        //设置渠道选择的模型
        request.setModel(channel.getModel());


        if (!channel.getType().equals(ChannelType.OPEN_AI.getType())) {
            List<OpenAiRequestBody.Message> originalMessages = request.getMessages();
            List<OpenAiRequestBody.Message> messages = IntStream.range(0, originalMessages.size())
                    .mapToObj(i -> {
                        OpenAiRequestBody.Message message = originalMessages.get(i);
                        if (message.getRole().equals(ChatRoleEnum.SYSTEM.getRole())) {
                            OpenAiRequestBody.Message userMessage = message.builder(ChatRoleEnum.USER.getRole(), message.getContent());
                            // 如果当前消息不是最后一个消息，那么添加一个新的助手消息
                            if (i < originalMessages.size() - 1) {
                                OpenAiRequestBody.Message assistantMessage = message.builder(ChatRoleEnum.ASSISTANT.getRole(), "好的");
                                return Arrays.asList(userMessage, assistantMessage);
                            } else {
                                return Collections.singletonList(userMessage);
                            }
                        } else {
                            return Collections.singletonList(message);
                        }
                    })
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            request.setMessages(messages);
        }

        //选择是否启用流式响应
        return request.isStream() ?
                streamCompletions(request, channel.getBaseUrl(), channel.getApiKey(), channel.isEnableProxy())
                : completions(request, channel.getBaseUrl(), channel.getApiKey(), channel.isEnableProxy());
    }

    private void validateAndSetParameters(OpenAiRequestBody request) {
        if(request.getTop_p() != null){
            if (request.getTop_p() >= 1) {
                request.setTop_p(0.7F);
            }
        }
//        if(request.getTemperature() != null){
//            if (request.getTemperature() >= 1 || request.getTemperature() == 0.0) {
//                request.setTemperature(0.95F);
//            }
//        }

    }
}
