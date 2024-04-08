package com.hjong.OnChat.adapter.openai;

import com.hjong.OnChat.adapter.Adapter;
import com.hjong.OnChat.entity.enums.ChannelType;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import static com.hjong.OnChat.adapter.Constants.OPEN_AI;
import static com.hjong.OnChat.adapter.Constants.ZHIPU;
import static com.hjong.OnChat.entity.enums.UrlEnums.getUrlByType;

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
                .flux();
    }


    @Override
    protected Flux<String> streamCompletions(OpenAiRequestBody request, String url, String apikey, Boolean enableProxy) {
        return getWebClient(enableProxy).post()
                .uri(super.buildEndpoint(url, OPEN_AI))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apikey)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class);
    }


}
