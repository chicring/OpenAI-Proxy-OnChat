package com.hjong.OnChat.chain.retrieve;

import com.hjong.OnChat.chain.prompt.PromptTemplate;
import com.hjong.OnChat.chain.vectorizer.VectorizationFactory;
import com.hjong.OnChat.chain.vectorstore.VectorStore;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/15
 **/

@Component
public class Retrieve {

    @Resource
    VectorStore VectorStore;

    @Resource
    VectorizationFactory vectorizationFactory;

    @Resource
    PromptTemplate promptTemplate;


    public Mono<String> retrieveknowledge(List<String> question, String collectionName, String key){

        return vectorizationFactory.getEmbedding("zhipu")
                .doVectorization(question,key)
                .collectList()
                .flatMap(v -> {
                    List<List<String>> knowledge = VectorStore.nearest(v,collectionName);

                    StringBuilder answer = new StringBuilder();

                    Random rand = new Random();

                    knowledge.forEach(sublist -> {
                        int sublistSize = sublist.size();

                        if (sublistSize < 10) {
                            sublist.forEach(answer::append);
                        } else {
                            sublist.subList(0, 8).forEach(answer::append);

                            List<String> remainingItems = new ArrayList<>(sublist.subList(5, sublistSize));
                            Collections.shuffle(remainingItems, rand);

                            remainingItems.subList(0, Math.min(3, remainingItems.size())).forEach(answer::append);
                        }
                    });

                    return Mono.just(promptTemplate.knowledgePrompt(answer.toString(),question.getLast()));
                });
    }
}
