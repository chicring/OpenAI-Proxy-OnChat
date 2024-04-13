package com.hjong.OnChat.chain.vectorizer;

import com.hjong.OnChat.chain.vectorizer.zhipu.ZhipuVectorization;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import static com.hjong.OnChat.chain.vectorizer.Consts.ZHIPU_EMBEDDING;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/12
 **/
@Component
public class VectorizationFactory {

    @Resource
    private ZhipuVectorization zhipuVectorization;

    public Vectorization getEmbedding(String type) {
        if (type.equals(ZHIPU_EMBEDDING)) {
            return zhipuVectorization;
        }
        return null;
    }
}
