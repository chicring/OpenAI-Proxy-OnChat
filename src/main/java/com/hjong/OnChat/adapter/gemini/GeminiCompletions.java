package com.hjong.OnChat.adapter.gemini;

import com.hjong.OnChat.adapter.Adapter;
import com.hjong.OnChat.adapter.openai.OpenAiRequestBody;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import static com.hjong.OnChat.adapter.Consts.GEMINI;
import static com.hjong.OnChat.entity.Consts.DONE;
import static com.hjong.OnChat.entity.enums.UrlEnums.getUrlByType;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/

@Component("gemini")
public class GeminiCompletions extends Adapter {


    @Override
    protected Flux<String> completions(OpenAiRequestBody request, String url, String apikey, Boolean enableProxy) {

        return getWebClient(enableProxy).post()
                .uri(this.endpoint(url, apikey, request.getModel(), false))
                .bodyValue(GeminiRequestBody.builder(request))
                .retrieve()
                .bodyToFlux(String.class)
                .retry(3)
                .filter((str) -> str.contains("text"))
                .flatMap(GeminiResponseBody::GeminiToOpenAI);
    }


    @Override
    protected Flux<String> streamCompletions(OpenAiRequestBody request, String url, String apikey, Boolean enableProxy) {

        return getWebClient(enableProxy).post()
                .uri(this.endpoint(url, apikey, request.getModel(), true))
                .bodyValue(GeminiRequestBody.builder(request))
                .retrieve()
                .bodyToFlux(String.class)
                .retry(3)
                .filter((str) -> str.contains("text"))
                .flatMap(GeminiResponseBody::GeminiToOpenAI)
                .concatWithValues(DONE);

    }

    private String endpoint(String baseurl, String apikey, String model, Boolean isStream) {

        return baseurl +
                getUrlByType(GEMINI) +
                "/" +
                model +
                (isStream ? ":streamGenerateContent" : ":generateContent") +
                "?key=" +
                apikey;
    }

}
