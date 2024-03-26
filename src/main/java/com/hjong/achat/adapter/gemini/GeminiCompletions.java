package com.hjong.achat.adapter.gemini;

import com.fasterxml.jackson.databind.JsonNode;
import com.hjong.achat.adapter.Adapter;
import com.hjong.achat.adapter.openai.OpenAiRequestBody;
import com.hjong.achat.adapter.openai.OpenAiResponseBody;
import com.hjong.achat.entity.DTO.Channel;
import com.hjong.achat.enums.ChatRoleEnum;
import com.hjong.achat.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/

@Component("gemini")
public class GeminiCompletions extends Adapter {
    public GeminiCompletions(WebClient webClient, WebClient webClientEnableProxy) {
        super(webClient, webClientEnableProxy);
    }

    @Override
    protected Flux<String> completions(OpenAiRequestBody request, Channel channel, WebClient webClient) {

        return webClient.post()
                .uri(url)
                .bodyValue(GeminiRequestBody.builder(request))
                .retrieve()
                .bodyToFlux(String.class)
                .filter((str) -> str.contains("text"))
                .flatMap(GeminiResponseBody::GeminiToOpenAI);
    }


    @Override
    protected Flux<String> streamCompletions(OpenAiRequestBody request, Channel channel,WebClient webClient) {


        return webClient.post()
                .uri(url)
                .bodyValue(GeminiRequestBody.builder(request))
                .retrieve()
                .bodyToFlux(String.class)
                .filter((str) -> str.contains("text"))
                .flatMap(GeminiResponseBody::GeminiToOpenAI)
                .concatWithValues("[DONE]");

    }

}
