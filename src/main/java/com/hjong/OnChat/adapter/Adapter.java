package com.hjong.OnChat.adapter;

import com.hjong.OnChat.adapter.openai.OpenAiRequestBody;
import com.hjong.OnChat.entity.dto.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;


import static com.hjong.OnChat.adapter.UrlEnums.getUrlByType;

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
