package com.hjong.achat.adapter.ai360;

import com.hjong.achat.adapter.Adapter;
import com.hjong.achat.adapter.gemini.GeminiRequestBody;
import com.hjong.achat.adapter.gemini.GeminiResponseBody;
import com.hjong.achat.adapter.openai.OpenAiRequestBody;
import com.hjong.achat.entity.DTO.Channel;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/26
 **/

@Component("ai360")
public class ai360Completions extends Adapter {
    public ai360Completions(WebClient webClient, WebClient webClientEnableProxy) {
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

        return webClient.post()
                .uri(super.url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + channel.getApiKey())
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class);
    }
}
