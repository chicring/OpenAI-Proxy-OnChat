package com.hjong.OnChat.service.impl;

import com.hjong.OnChat.chain.loader.ResourceLoader;
import com.hjong.OnChat.chain.loader.ResourseLoaderFactory;
import com.hjong.OnChat.chain.vectorizer.VectorizationFactory;
import com.hjong.OnChat.chain.vectorstore.MilvusVectorStore;
import com.hjong.OnChat.entity.dto.File;
import com.hjong.OnChat.entity.dto.KnowledgeBase;
import com.hjong.OnChat.entity.enums.ServiceExceptionEnum;
import com.hjong.OnChat.entity.vo.req.KnowledgeBaseVO;
import com.hjong.OnChat.entity.vo.req.KnowledgeUploadVO;
import com.hjong.OnChat.exception.ServiceException;
import com.hjong.OnChat.repositories.FileRepositories;
import com.hjong.OnChat.repositories.KnowledgeBaseRepositories;
import com.hjong.OnChat.service.KnowledgeBaseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;


/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/14
 **/
@Slf4j
@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    @Resource
    KnowledgeBaseRepositories knowledgeBaseRepository;

    @Resource
    FileRepositories fileRepositories;

    @Resource
    ResourseLoaderFactory resourseLoaderFactory;

    @Resource
    MilvusVectorStore milvusVectorStore;

    @Resource
    VectorizationFactory vectorizationFactory;

    @Resource
    DatabaseClient databaseClient;

    @Value("${chat.vector.embedding.type}")
    private String VectorType;

    @Override
    public Flux<KnowledgeBase> find() {
        return knowledgeBaseRepository.findAll();
    }

    @Override
    public Flux<File> findFile(Integer knowledgeBaseId) {
        return fileRepositories.findByKnowledgeBaseId(knowledgeBaseId);
    }

    @Override
    public Mono<KnowledgeBase> save(KnowledgeBaseVO vo, Integer userId) {
        KnowledgeBase knowledgeBase = new KnowledgeBase();
        knowledgeBase.setName(vo.getName());
        knowledgeBase.setUserId(userId);
        knowledgeBase.setCollectionName(vo.getCollectionName());
        knowledgeBase.setDescription(vo.getDescription());

        //在数据库中创建知识库集合
        milvusVectorStore.createCollection(vo.getCollectionName());

        return knowledgeBaseRepository.save(knowledgeBase);
    }

    @Transactional
    @Override
    public Mono<Void> delete(Integer id) {
        return knowledgeBaseRepository.deleteById(id)
                .then(fileRepositories.deleteByKnowledgeBaseId(id));
    }

    @Override
    @Transactional
    public Mono<Void> deleteFile(String collectionName, Integer fid) {
        Mono<Void> deleteFromDb = fileRepositories.deleteById(fid);
        Mono<Void> deleteFromMilvus = Mono.defer(() ->
                Mono.fromRunnable(() -> milvusVectorStore.deleteByFidAndCollectionName(collectionName, fid))
        );
        return deleteFromDb.then(deleteFromMilvus);
    }


    @Override
    public Mono<Void> update(String fileName,Integer status) {
        return fileRepositories.updateStatusByName(fileName,status);
    }


    @Override
    public Mono<Void> upload(KnowledgeUploadVO vo){

        return this.getCollectionNameById(vo.getId())
                .switchIfEmpty(Mono.error(new ServiceException(ServiceExceptionEnum.KNOWLEDGE_BASE_NOT_EXIST)))
                .doOnNext(collectionName -> {
                    storeContent(vo.getFile(), vo.getId(),collectionName)
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe();
                })
                .then();
    }


    private Mono<Void> storeContent(FilePart file,Integer knowledgeBaseId,String collectionName) {

        String fileName = file.filename();
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        log.debug("fileType: {}", fileType);
        log.debug("collectionName: {}", collectionName);

        File fileInfo = new File();
        fileInfo.setName(fileName);
        fileInfo.setKnowledgeBaseId(knowledgeBaseId);

        fileRepositories.save(fileInfo)
                .flatMap(f -> {
                    fileInfo.setId(f.getId());
                    return Mono.just(f);
                }).subscribe();

        return file.content()
                .collectList()
                .flatMap(dataBuffers -> {
                    Flux<DataBuffer> dataBufferFlux = Flux.fromIterable(dataBuffers);
                    return DataBufferUtils.join(dataBufferFlux);
                })
                .map(joinedDataBuffer -> {
                    InputStream inputStream = joinedDataBuffer.asInputStream();
                    ResourceLoader loader = resourseLoaderFactory.getLoader(fileType);
                    String content = loader.getContent(inputStream);
                    return loader.getChunkList(content);
                })
                .flatMap(chunkList -> this.getApiKeyByType(VectorType)
                        .flatMap(apikey -> {
                            log.debug("apikey: {}", apikey);
                            return vectorizationFactory.getEmbedding(VectorType).doVectorization(chunkList, apikey)
                                    .collectList()
                                    .doOnNext(v -> log.info("开始存储向量..."))
                                    .flatMap(v -> Mono.fromRunnable(() -> milvusVectorStore.storeEmbeddings(chunkList, v, collectionName, fileInfo.getId())))
                                    .subscribeOn(Schedulers.boundedElastic());
                        }))
                .then(update(fileName, 1))
                .onErrorResume(error -> update(fileName, 2));
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
