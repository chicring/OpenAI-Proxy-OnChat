package com.hjong.OnChat.chain.vectorstore;


import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.grpc.SearchResults;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.*;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;

import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;

import jakarta.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.milvus.param.Constant.INDEX_TYPE;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/14
 **/
@Slf4j
@Component
public class MilvusVectorStore implements VectorStore {

    @Value("${milvus.host}")
    private String milvusHost;
    @Value("${milvus.port}")
    private Integer milvausPort;
    @Value("${milvus.username}")
    private String milvusUserName;
    @Value("${milvus.password}")
    private String milvusPassword;

    private MilvusServiceClient milvusServiceClient;

    @PostConstruct
    public void init(){
        milvusServiceClient = new MilvusServiceClient(
                ConnectParam.newBuilder()
                        .withHost(milvusHost)
                        .withPort(milvausPort)
                        .withAuthorization(milvusUserName, milvusPassword)
                        .build()
        );
    }

    public void createCollection(String collectionName) {

        FieldType primaryField = FieldType.newBuilder()
                .withName("id")
                .withDescription("主键id")
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .withAutoID(true)
                .build();

        FieldType fileField = FieldType.newBuilder()
                .withName("fid")
                .withDescription("文件id")
                .withDataType(DataType.Int64)
                .build();

        FieldType contentWordCountField = FieldType.newBuilder()
                .withName("content_word_count")
                .withDataType(DataType.Int32)
                .build();

        FieldType contentField = FieldType.newBuilder()
                .withName("content")
                .withDataType(DataType.VarChar)
                .withMaxLength(2000)
                .build();

        FieldType vectorField = FieldType.newBuilder()
                .withName("vector")
                .withDataType(DataType.FloatVector)
                .withDimension(1024)
                .build();

        milvusServiceClient.createCollection(
                CreateCollectionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .withShardsNum(2)
                        .addFieldType(primaryField)
                        .addFieldType(fileField)
                        .addFieldType(contentWordCountField)
                        .addFieldType(contentField)
                        .addFieldType(vectorField)
                        .withEnableDynamicField(true)
                        .build()
        );

        milvusServiceClient.createIndex(
                CreateIndexParam.newBuilder()
                        .withCollectionName(collectionName)
                        .withFieldName("vector")
                        .withMetricType(MetricType.L2)
                        .withSyncMode(Boolean.FALSE)
                        .build()
        );

    }

    @Override
    public void storeEmbeddings(List<String> chunkList, List<List<Double>> vectorList,
                                String collectionName, Integer fileId) {

        log.debug("字符数组长度：{}, 向量数字长度: {}",chunkList.size(),vectorList.size());

        if(chunkList.size() != vectorList.size()) {
            log.error("字符数组长度与向量数组长度不一致");
            return;
        }

        List<Integer> contentWordCount = chunkList.stream()
                .map(String::length)
                .collect(Collectors.toList());

        List<List<Float>> vectorFloatList = vectorList.stream()
                .map(vector -> vector.stream()
                        .map(Double::floatValue)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        List<Long> fileIdList = new ArrayList<>(Collections.nCopies(chunkList.size(), fileId.longValue()));
        List<InsertParam.Field> fields = new ArrayList<>();

        fields.add(new InsertParam.Field("fid", fileIdList));
        fields.add(new InsertParam.Field("content", chunkList));
        fields.add(new InsertParam.Field("content_word_count", contentWordCount));
        fields.add(new InsertParam.Field("vector", vectorFloatList));

        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(collectionName)
                .withFields(fields)
                .build();

        try {
            milvusServiceClient.insert(insertParam);
            log.info("向量存储成功");
        }catch (Exception e){
            log.error("向量存储失败",e);
        }
    }

    @Override
    public List<List<String>> nearest(List<List<Double>> queryVectorsList, String collectionName) {

        milvusServiceClient.loadCollection(
                LoadCollectionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build()
        );

        List<List<Float>> queryVectors = queryVectorsList.stream()
                .map(vector -> vector.stream()
                        .map(Double::floatValue)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        String search_param = "{\"nprobe\":10, \"offset\":0}";

        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(collectionName)
                .withOutFields(List.of("content", "content_word_count", "vector"))
                .withVectors(queryVectors)
                .withTopK(50)
                .withParams(search_param)
                .withVectorFieldName("vector")
                .build();

        R<SearchResults> respSearch = milvusServiceClient.search(searchParam);
        if (respSearch.getStatus() != R.Status.Success.getCode()) {
            return List.of();
        }
        SearchResultsWrapper searchResultsWrapper = new SearchResultsWrapper(respSearch.getData().getResults());

        int numQueries = queryVectors.size();

        List<List<String>> resultlist = new ArrayList<>();
        List<String> result = new ArrayList<>();

        for (int i = 0; i < numQueries; i++) {
            List<QueryResultsWrapper.RowRecord> rowRecords = searchResultsWrapper.getRowRecords(i);
            for (QueryResultsWrapper.RowRecord rowRecord : rowRecords) {
                String content = rowRecord.get("content").toString();
                result.add(content);
            }
            resultlist.add(result);
        }
        milvusServiceClient.releaseCollection(
                ReleaseCollectionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build()
        );
        return resultlist;
    }

    @Override
    public void deleteCollection(String collectionName) {
        milvusServiceClient.dropCollection(
                DropCollectionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build()
        );
    }

    @Override
    public void deleteByFidAndCollectionName(String collectionName, Integer fileId) {
        milvusServiceClient.delete(
                DeleteParam.newBuilder()
                        .withCollectionName(collectionName)
                        .withExpr("fid == " + fileId)
                        .build()
        );
    }


}
