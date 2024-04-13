package com.hjong.OnChat.chain.vectorizer.zhipu;

import com.hjong.OnChat.chain.vectorizer.Vectorization;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;


import java.util.List;

import static com.hjong.OnChat.chain.vectorizer.Consts.ZHIPU_EMBEDDING_API;
import static com.hjong.OnChat.chain.vectorizer.Consts.ZHIPU_EMBEDDING_MODEL;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/12
 **/

@Component
public class ZhipuVectorization implements Vectorization {

    @Resource
    WebClient webClient;

    @Override
    public Flux<List<Double>> doVectorization(List<String> chunkList, String apikey) {

        return Flux.fromIterable(chunkList)
                .flatMap(chunk -> {
                    RequestBody requestBody = new RequestBody();
                    requestBody.setInput(chunk);
                    requestBody.setModel(ZHIPU_EMBEDDING_MODEL);
                    return webClient.post()
                            .uri(ZHIPU_EMBEDDING_API)
                            .header("Authorization", apikey)
                            .bodyValue(requestBody)
                            .retrieve()
                            .bodyToMono(ResponseBody.class)
                            .map(responseBody -> responseBody.getData().getFirst().getEmbedding());
                });
    }
}
