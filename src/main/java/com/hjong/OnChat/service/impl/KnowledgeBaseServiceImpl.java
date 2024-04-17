package com.hjong.OnChat.service.impl;

import com.hjong.OnChat.chain.loader.ResourseLoaderFactory;
import com.hjong.OnChat.chain.vectorizer.VectorizationFactory;
import com.hjong.OnChat.chain.vectorstore.MilvusVectorStore;
import com.hjong.OnChat.entity.dto.KnowledgeBase;
import com.hjong.OnChat.entity.vo.req.KnowledgeBaseVO;
import com.hjong.OnChat.entity.vo.req.KnowledgeUploadVO;
import com.hjong.OnChat.repositories.ChannelRepositories;
import com.hjong.OnChat.repositories.KnowledgeBaseRepositories;
import com.hjong.OnChat.service.KnowledgeBaseService;
import jakarta.annotation.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/14
 **/

@Component
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    @Resource
    KnowledgeBaseRepositories knowledgeBaseRepository;

    @Resource
    ChannelRepositories channelRepositories;

    @Resource
    ResourseLoaderFactory resourseLoaderFactory;

    @Resource
    MilvusVectorStore milvusVectorStore;

    @Resource
    VectorizationFactory vectorizationFactory;


    private final String VectorType = "zhipu";

    @Override
    public Flux<KnowledgeBase> find() {
        return knowledgeBaseRepository.findAll();
    }

    @Override
    public Mono<KnowledgeBase> save(KnowledgeBaseVO vo, Integer userId) {
        KnowledgeBase knowledgeBase = new KnowledgeBase();
        knowledgeBase.setName(vo.getName());
        knowledgeBase.setUserId(userId);
        knowledgeBase.setCollectionName(vo.getCollectionName());
        knowledgeBase.setDescription(vo.getDescription());
        knowledgeBase.setStatus(0);

        return knowledgeBaseRepository.save(knowledgeBase);
    }

    @Override
    public Mono<Void> delete(Integer id) {
        return null;
    }

    @Override
    public Mono<Void> update(String CollectionName,Integer status) {
        return knowledgeBaseRepository.updateStatusByVectorCollectionName(CollectionName,status);
    }


    @Override
    public Mono<Void> upload(KnowledgeUploadVO vo){
        return knowledgeBaseRepository.getCollectionNameById(vo.getId())
                .flatMap(collectionName -> storeContent(vo.getFile(), collectionName))
                .publishOn(Schedulers.boundedElastic());
    }

    private Mono<Void> storeContent(FilePart file, String collectionName) {

        String fileName = file.filename();
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        return file.content()
                .map(DataBuffer::asInputStream)
                .map(inputStream -> resourseLoaderFactory.getLoader(fileType).getChunkList(resourseLoaderFactory.getLoader(fileType).getContent(inputStream)))
                .flatMap(chunkList -> {
                    String apikey = channelRepositories.getApiKeyByType(VectorType).block();

                    return Mono.zip(
                            Mono.just(chunkList),
                            Mono.just(vectorizationFactory.getEmbedding(VectorType).doVectorization(chunkList, apikey))
                    );
                })
                .flatMap(tuple -> {
                    List<String> chunkList = tuple.getT1();
                    List<List<Double>> v = tuple.getT2().collectList().block();
                    milvusVectorStore.storeEmbeddings(chunkList, v, collectionName);
                    return Mono.empty();
                })
                .then(update(collectionName, 1))
                .onErrorResume(error -> update(collectionName, 2));
    }

}
