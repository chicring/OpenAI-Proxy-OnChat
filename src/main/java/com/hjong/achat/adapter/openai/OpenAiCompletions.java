package com.hjong.achat.adapter.openai;

import com.hjong.achat.adapter.Adapter;
import com.hjong.achat.entity.DTO.Channel;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/
@Component("openai")
public class OpenAiCompletions extends Adapter {

    public OpenAiCompletions(WebClient webClient, WebClient webClientEnableProxy) {
        super(webClient, webClientEnableProxy);
    }

    @Override
    protected Flux<String> completions(OpenAiRequestBody request, Channel channel, WebClient webClient) {
        return webClient.post()
                .uri(super.url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + channel.getApiKey())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .flux();
    }


    @Override
    protected Flux<String> streamCompletions(OpenAiRequestBody request, Channel channel, WebClient webClient) {
        return webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + channel.getApiKey())
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class);
    }
}
