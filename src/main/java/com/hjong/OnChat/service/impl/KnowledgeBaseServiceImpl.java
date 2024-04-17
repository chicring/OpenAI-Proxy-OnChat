package com.hjong.OnChat.service.impl;

import com.hjong.OnChat.entity.dto.KnowledgeBase;
import com.hjong.OnChat.entity.vo.req.KnowledgeBaseVO;
import com.hjong.OnChat.entity.vo.req.KnowledgeUploadVO;
import com.hjong.OnChat.repositories.KnowledgeBaseRepositories;
import com.hjong.OnChat.service.KnowledgeBaseService;
import jakarta.annotation.Resource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/14
 **/

@Component
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    @Resource
    KnowledgeBaseRepositories knowledgeBaseRepository;

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
        knowledgeBaseRepository.getCollectionNameById(vo.getId())
                .flatMap(collectionName -> storeContent(vo.getFile(), collectionName))
                .then();
    }

    private Mono<Void> storeContent(FilePart file, String collectionName) {


        return Mono.empty();
    }

}
