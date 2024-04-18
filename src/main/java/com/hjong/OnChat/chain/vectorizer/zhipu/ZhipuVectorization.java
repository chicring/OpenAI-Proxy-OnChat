package com.hjong.OnChat.chain.vectorizer.zhipu;

import com.hjong.OnChat.chain.vectorizer.Vectorization;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hjong.OnChat.chain.vectorizer.Consts.ZHIPU_EMBEDDING_API;
import static com.hjong.OnChat.chain.vectorizer.Consts.ZHIPU_EMBEDDING_MODEL;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/12
 **/

@Slf4j
@Component
public class ZhipuVectorization implements Vectorization {

    @Resource
    WebClient webClient;

    @Override
    public Flux<List<Double>> doVectorization(List<String> chunkList, String apikey) {
        int size = chunkList.size();
        log.info("需要请求数量：{}", size);
        AtomicInteger i = new AtomicInteger(1);

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
                            .retryWhen(Retry.backoff(5, Duration.ofSeconds(2))
                                    .maxBackoff(Duration.ofSeconds(5))
                                    .jitter(0.5))
                            .doOnNext(responseBody -> log.info("进度：{} / {}, 消耗token：{}", i.getAndIncrement(), size ,responseBody.getUsage().getTotal_tokens()))
                            .map(responseBody -> responseBody.getData().getFirst().getEmbedding())
                            .onErrorResume(throwable -> {
                                log.error("请求失败，重试中: {}", throwable.getMessage());
                                return Mono.error(throwable);
                            });
                },5);
    }

    @Override
    public Mono<List<Double>> singleVectorization(String chunk, String apikey) {
        List<String> chunkList = new ArrayList<>();
        chunkList.add(chunk);
        return doVectorization(chunkList, apikey).single();
    }

}
