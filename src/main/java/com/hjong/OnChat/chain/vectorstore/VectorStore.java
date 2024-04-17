package com.hjong.OnChat.chain.vectorstore;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/12
 **/
public interface VectorStore {

    /**
     * Store embeddings.
     *
     * @param chunkList   内容list
     * @param vectorList  向量list
     * @param collectionName 向量集合名称
     */
    void storeEmbeddings(List<String> chunkList, List<List<Double>> vectorList, String collectionName);

    List<List<String>> nearest(List<List<Double>> queryVectorsList,String collectionName);
}
