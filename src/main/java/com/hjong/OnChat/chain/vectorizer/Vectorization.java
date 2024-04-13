package com.hjong.OnChat.chain.vectorizer;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/12
 **/
public interface Vectorization {

    /**
     * @param chunkList 切割后的文本列表
     * @param apikey API key
     * @return 返回文本列表的向量化结果
     */
    public Flux<List<Double>> doVectorization(List<String> chunkList, String apikey);
}
