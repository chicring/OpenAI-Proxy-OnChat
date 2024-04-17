package com.hjong.OnChat.repositories;

import com.hjong.OnChat.entity.dto.KnowledgeBase;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/14
 **/

@Repository
public interface KnowledgeBaseRepositories extends R2dbcRepository<KnowledgeBase,Integer> {

    @Query("update knowledgeBase set status = :status where collection_name = :CollectionName")
    Mono<Void> updateStatusByVectorCollectionName(String CollectionName, Integer status);


    Mono<String> getCollectionNameById(Integer id);
}