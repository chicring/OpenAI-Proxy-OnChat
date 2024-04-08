package com.hjong.OnChat.adapter.ai360;

import com.hjong.OnChat.adapter.Adapter;
import com.hjong.OnChat.adapter.openai.OpenAiRequestBody;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import static com.hjong.OnChat.adapter.Constants.AI_360;
import static com.hjong.OnChat.adapter.Constants.ZHIPU;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/26
 **/

@Component("ai360")
public class ai360Completions extends Adapter {

    @Override
    protected Flux<String> completions(OpenAiRequestBody request, String url, String apikey, Boolean enableProxy) {

        return getWebClient(enableProxy).post()
                .uri(super.buildEndpoint(url, AI_360))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apikey)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class);
    }

    @Override
    protected Flux<String> streamCompletions(OpenAiRequestBody request, String url, String apikey, Boolean enableProxy) {

        return getWebClient(enableProxy).post()
                .uri(super.buildEndpoint(url, AI_360))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apikey)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class);
    }
}
