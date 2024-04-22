package com.hjong.OnChat.adapter.openai;

import com.hjong.OnChat.adapter.Adapter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import static com.hjong.OnChat.adapter.Consts.OPEN_AI;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/
@Component("openai")
public class OpenAiCompletions extends Adapter {



    @Override
    protected Flux<String> completions(OpenAiRequestBody request,String url, String apikey, Boolean enableProxy) {
        return getWebClient(enableProxy).post()
                .uri(super.buildEndpoint(url, OPEN_AI))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apikey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .retry(3)
                .flux();
    }


    @Override
    protected Flux<String> streamCompletions(OpenAiRequestBody request, String url, String apikey, Boolean enableProxy) {
        return getWebClient(enableProxy).post()
                .uri(super.buildEndpoint(url, OPEN_AI))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apikey)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .retry(3);
    }


}
