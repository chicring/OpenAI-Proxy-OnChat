package com.hjong.OnChat.adapter.ai360;

import com.hjong.OnChat.adapter.Adapter;
import com.hjong.OnChat.adapter.openai.OpenAiRequestBody;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import static com.hjong.OnChat.adapter.Consts.AI_360;

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
                .bodyValue(Ai360RequestBody.builder(request))
                .retrieve()
                .bodyToFlux(String.class)
                .retry(3);
    }

    @Override
    protected Flux<String> streamCompletions(OpenAiRequestBody request, String url, String apikey, Boolean enableProxy) {

        return getWebClient(enableProxy).post()
                .uri(super.buildEndpoint(url, AI_360))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apikey)
                .bodyValue(Ai360RequestBody.builder(request))
                .retrieve()
                .bodyToFlux(String.class)
                .retry(3);
    }
}
