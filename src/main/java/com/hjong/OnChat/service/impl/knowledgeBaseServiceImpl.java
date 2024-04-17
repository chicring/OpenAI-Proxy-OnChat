package com.hjong.OnChat.service.impl;

import com.hjong.OnChat.entity.dto.KnowledgeBase;
import com.hjong.OnChat.entity.vo.req.KnowledgeBaseVO;
import com.hjong.OnChat.repositories.KnowledgeBaseRepositories;
import com.hjong.OnChat.service.knowledgeBaseService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/14
 **/

@Component
public class knowledgeBaseServiceImpl implements knowledgeBaseService {

    @Resource
    KnowledgeBaseRepositories knowledgeBaseRepository;

    @Override
    public Flux<KnowledgeBase> find() {
        return knowledgeBaseRepository.findAll();
    }

    @Override
    public Mono<KnowledgeBase> save(KnowledgeBaseVO vo) {
        KnowledgeBase knowledgeBase = new KnowledgeBase();
        knowledgeBase.setName(vo.getName());
        knowledgeBase.setUserId(vo.getUserId());
        knowledgeBase.setVectorCollectionName(vo.getVectorCollectionName());
        knowledgeBase.setDescription(vo.getDescription());
        knowledgeBase.setStatus(0);

        return knowledgeBaseRepository.save(knowledgeBase);
    }

    @Override
    public Mono<Void> delete(Integer id) {
        return null;
    }

    @Override
    public Mono<Void> update(String vectorCollectionName,Integer status) {
        return knowledgeBaseRepository.updateStatusByVectorCollectionName(vectorCollectionName,status);
    }
}
