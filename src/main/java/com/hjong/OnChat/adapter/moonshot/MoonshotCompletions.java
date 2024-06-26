package com.hjong.OnChat.adapter.moonshot;

import com.hjong.OnChat.adapter.Adapter;
import com.hjong.OnChat.adapter.openai.OpenAiRequestBody;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import static com.hjong.OnChat.adapter.Consts.MOONSHOT;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/9
 **/
@Component("moonshot")
public class MoonshotCompletions extends Adapter {
    @Override
    protected Flux<String> completions(OpenAiRequestBody request, String url, String apikey, Boolean enableProxy) {

        return getWebClient(enableProxy).post()
                .uri(buildEndpoint(url, MOONSHOT))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apikey)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .retry(3);
    }

    @Override
    protected Flux<String> streamCompletions(OpenAiRequestBody request,  String url, String apikey, Boolean enableProxy) {

        return getWebClient(enableProxy).post()
                .uri(buildEndpoint(url, MOONSHOT))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apikey)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .retry(3);
    }
}
