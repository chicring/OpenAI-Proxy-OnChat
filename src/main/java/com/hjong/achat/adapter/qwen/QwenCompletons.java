package com.hjong.achat.adapter.qwen;

import com.hjong.achat.adapter.Adapter;
import com.hjong.achat.adapter.openai.OpenAiRequestBody;
import com.hjong.achat.entity.DTO.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import static com.hjong.achat.adapter.qwen.QwenResponseBody.QwenToOpenAI;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/17
 **/
@Slf4j
@Component("qwen")
public class QwenCompletons extends Adapter {
    public QwenCompletons(WebClient webClient, WebClient webClientEnableProxy) {
        super(webClient, webClientEnableProxy);
    }

    @Override
    protected Flux<String> completions(OpenAiRequestBody request, Channel channel, WebClient webClient) {

        return webClient.post()
                .uri(super.url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + channel.getApiKey())
                .bodyValue(QwenRequestBody.builder(request))
                .retrieve()
                .bodyToFlux(QwenResponseBody.class)
                .flatMap(response -> QwenToOpenAI(response,request.getModel()));
    }

    @Override
    protected Flux<String> streamCompletions(OpenAiRequestBody request, Channel channel, WebClient webClient) {

        return webClient.post()
                .uri(super.url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + channel.getApiKey())
                .header(HttpHeaders.ACCEPT, "text/event-stream")
                .bodyValue(QwenRequestBody.builder(request))
                .retrieve()
                .bodyToFlux(QwenResponseBody.class)
                .flatMap(response -> QwenToOpenAI(response,request.getModel()))
                .concatWithValues("[DONE]");
    }

}
