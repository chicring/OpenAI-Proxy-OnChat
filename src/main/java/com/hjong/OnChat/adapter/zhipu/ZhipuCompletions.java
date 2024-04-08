package com.hjong.OnChat.adapter.zhipu;

import com.hjong.OnChat.adapter.Adapter;
import com.hjong.OnChat.adapter.openai.OpenAiRequestBody;
import com.hjong.OnChat.entity.enums.ChannelType;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import static com.hjong.OnChat.adapter.Constants.ZHIPU;
import static com.hjong.OnChat.entity.enums.UrlEnums.getUrlByType;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/18
 **/
@Component("zhipu")
public class ZhipuCompletions extends Adapter {


    @Override
    protected Flux<String> completions(OpenAiRequestBody request, String url, String apikey, Boolean enableProxy) {

        return getWebClient(enableProxy).post()
                .uri(buildEndpoint(url, ZHIPU))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apikey)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class);
    }

    @Override
    protected Flux<String> streamCompletions(OpenAiRequestBody request,  String url, String apikey, Boolean enableProxy) {

        return getWebClient(enableProxy).post()
                .uri(buildEndpoint(url, ZHIPU))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apikey)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class);
    }

}
