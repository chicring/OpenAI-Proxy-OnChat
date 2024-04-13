package com.hjong.OnChat.util;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/11
 **/

@Slf4j
@Component
public class FlowUtils {

    @Resource
    ReactiveRedisTemplate<String,Long> reactiveRedisTemplate;

    /**
     *
     * @param key 键
     * @param blockTime 限流时间
     * @return 是否通过
     */
    public Mono<Boolean> limitOnceCheck(String key, int blockTime){
        return this.internalCheck(key, 1, blockTime);
    }



    private Mono<Boolean> internalCheck(String key, int frequency, int period){
        return reactiveRedisTemplate.opsForValue().get(key)
                .hasElement()
                .flatMap(hasKey -> {
                    if(!hasKey) {
                        return reactiveRedisTemplate.opsForValue()
                                .set(key, 1L, Duration.ofSeconds(period))
                                .then(Mono.just(true));
                    }else{
                        return reactiveRedisTemplate.opsForValue()
                                .increment(key)
                                .flatMap(value -> Mono.just(value < frequency));
                    }
                });

    }

}
