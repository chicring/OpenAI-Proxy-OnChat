package com.hjong.OnChat.service;

import com.hjong.OnChat.entity.dto.KnowledgeBase;
import com.hjong.OnChat.entity.vo.req.KnowledgeBaseVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/14
 **/
public interface knowledgeBaseService {




    /**
     * 查询所有的知识库
     *
     * @return knowledge base list
     */
    Flux<KnowledgeBase> find();

    /**
     * 增加知识库
     *
     * @param vo knowledge base id
     * @return knowledge base
     */
    Mono<KnowledgeBase> save(KnowledgeBaseVO vo);

    /**
     * 删除知识库
     *
     * @param id knowledge base id
     * @return knowledge base
     */
    Mono<Void> delete(Integer id);

    /**
     * 更新知识库状态 0 向量处理中，1 已完成 2 失败
     *
     * @param vectorCollectionName knowledge base id
     * @return knowledge base
     */
    Mono<Void> update(String vectorCollectionName, Integer status);
}
