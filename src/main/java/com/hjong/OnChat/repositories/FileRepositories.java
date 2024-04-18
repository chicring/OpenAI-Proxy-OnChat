package com.hjong.OnChat.repositories;

import com.hjong.OnChat.entity.dto.File;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/18
 **/

@Repository
public interface FileRepositories extends R2dbcRepository<File,Integer> {

    @Query("update Files set status = :status where name = :fileName")
    Mono<Void> updateStatusByName(String fileName, Integer status);


    Flux<File> findByKnowledgeBaseId(Integer knowledgeBaseId);
}
