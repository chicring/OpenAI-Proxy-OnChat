package com.hjong.OnChat.service;

import com.hjong.OnChat.entity.dto.File;
import com.hjong.OnChat.entity.dto.KnowledgeBase;
import com.hjong.OnChat.entity.vo.req.KnowledgeBaseVO;
import com.hjong.OnChat.entity.vo.req.KnowledgeUploadVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/14
 **/
public interface KnowledgeBaseService {


    /**
     * 查询所有的知识库
     *
     * @return knowledge base list
     */
    Flux<KnowledgeBase> find();

    /**
     * 查询所有的文件
     *
     * @return knowledge base list
     */
    Flux<File> findFile(Integer knowledgeBaseId);
    /**
     * 增加知识库
     *
     * @param vo knowledge base id
     * @return knowledge base
     */
    Mono<KnowledgeBase> save(KnowledgeBaseVO vo, Integer userId);

    /**
     * 删除知识库
     *
     * @param id knowledge base id
     * @return knowledge base
     */
    Mono<Void> delete(Integer id);

    /**
     * 删除文件
     *
     * @param fid knowledge base id
     * @return knowledge base
     */
    Mono<Void> deleteFile(String collectionName,Integer fid);

    /**
     * 更新知识库状态 0 向量处理中，1 已完成 2 失败
     *
     * @param vectorCollectionName knowledge base id
     * @return knowledge base
     */
    Mono<Void> update(String vectorCollectionName, Integer status);

    /**
     * 上传文件到向量数据库
     *
     * @param vo 知识库文件
     * @return knowledge base
     */
    Mono<Void> upload(KnowledgeUploadVO vo);
}
