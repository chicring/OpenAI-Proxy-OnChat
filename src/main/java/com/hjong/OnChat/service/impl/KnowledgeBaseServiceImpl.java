package com.hjong.OnChat.service.impl;

import com.hjong.OnChat.chain.loader.ResourceLoader;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;
import java.util.List;
import java.util.function.Function;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/14
 **/
@Slf4j
@Component
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    @Resource
    KnowledgeBaseRepositories knowledgeBaseRepository;

    @Resource
    ResourseLoaderFactory resourseLoaderFactory;

    @Resource
    MilvusVectorStore milvusVectorStore;

    @Resource
    VectorizationFactory vectorizationFactory;

    @Resource
    DatabaseClient databaseClient;

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
        return this.getCollectionNameById(vo.getId())
                .doOnNext(collectionName -> {
                    storeContent(vo.getFile(), collectionName)
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe();
                })
                .then();
    }

    private Mono<Void> storeContent(FilePart file, String collectionName) {

        String fileName = file.filename();
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        log.info("fileType: {}", fileType);
        log.info("collectionName: {}", collectionName);
        return file.content()
                .collectList()
                .flatMap(dataBuffers -> {
                    // 将List<DataBuffer>转换为Flux<DataBuffer>
                    Flux<DataBuffer> dataBufferFlux = Flux.fromIterable(dataBuffers);
                    // 使用DataBufferUtils.join方法连接DataBuffer
                    return DataBufferUtils.join(dataBufferFlux);
                })
                .map(joinedDataBuffer -> {
                    // 将所有的数据缓冲区连接成一个输入流
                    InputStream inputStream = joinedDataBuffer.asInputStream();
                    // 获取文件加载器并处理输入流
                    ResourceLoader loader = resourseLoaderFactory.getLoader(fileType);
                    String content = loader.getContent(inputStream);
                    return loader.getChunkList(content);
                })
                .flatMap(chunkList -> {
                    log.info("chunkList: {}", chunkList);
                    return this.getApiKeyByType(VectorType)
                            .flatMap(apikey -> {
                                log.info("apikey: {}", apikey);
                                return vectorizationFactory.getEmbedding(VectorType).doVectorization(chunkList, apikey)
                                        .collectList()
                                        .doOnNext(v -> log.info("开始存储向量..."))
                                        .flatMap(v -> Mono.fromRunnable(() -> milvusVectorStore.storeEmbeddings(chunkList, v, collectionName)))
                                        .subscribeOn(Schedulers.boundedElastic());
                            });
                })
                .then(update(collectionName, 1))
                .onErrorResume(error -> update(collectionName, 2));
    }

    private Mono<String> getApiKeyByType(String type){
        return databaseClient.sql("select api_key from Channel where type = :type")
                .bind("type", type)
                .fetch()
                .one()
                .map(result ->  result.get("api_key").toString());
    }


    private Mono<String> getCollectionNameById(Integer id){
        return databaseClient.sql("select collection_name from knowledgeBase where id = :id")
                .bind("id", id)
                .fetch()
                .one()
                .map(result ->  result.get("collection_name").toString());
    }
}
