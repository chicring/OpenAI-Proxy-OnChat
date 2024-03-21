package com.hjong.achat.adapter.zhipu;

import com.hjong.achat.adapter.Adapter;
import com.hjong.achat.adapter.openai.OpenAiRequestBody;
import com.hjong.achat.entity.DTO.Channel;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/18
 **/
@Component("zhipu")
public class ZhipuCompletions extends Adapter {
    public ZhipuCompletions(WebClient webClient, WebClient webClientEnableProxy) {
        super(webClient, webClientEnableProxy);
    }

    @Override
    protected Flux<String> completions(OpenAiRequestBody request, Channel channel, WebClient webClient) {

        return webClient.post()
                .uri(super.url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + channel.getApiKey())
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class);
    }

    @Override
    protected Flux<String> streamCompletions(OpenAiRequestBody request, Channel channel, WebClient webClient) {

        if(request.getTop_p() >= 1 ){
            request.setTop_p(0.99);
        }
        if(request.getTemperature() >= 1){
            request.setTemperature(0.99);
        }

        return webClient.post()
                .uri(super.url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + channel.getApiKey())
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class);
    }
}
