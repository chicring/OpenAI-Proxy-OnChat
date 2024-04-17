package com.hjong.OnChat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hjong.OnChat.chain.loader.PdfFileLoader;
import com.hjong.OnChat.chain.loader.ResourceLoader;
import com.hjong.OnChat.chain.loader.ResourseLoaderFactory;
import com.hjong.OnChat.chain.split.SpliterFactory;
import com.hjong.OnChat.chain.vectorizer.Vectorization;
import com.hjong.OnChat.chain.vectorizer.VectorizationFactory;
import com.hjong.OnChat.chain.vectorstore.MilvusVectorStore;
import com.hjong.OnChat.entity.dto.Model;
import com.hjong.OnChat.repositories.ModelRepository;
import com.hjong.OnChat.service.impl.ProxyServiceImpl;
import com.hjong.OnChat.util.FlowUtils;
import com.hjong.OnChat.util.JsonUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@SpringBootTest
class OnChatApplicationTests {

    @Resource
    ResourseLoaderFactory resourseLoaderFactory;

    @Resource
    MilvusVectorStore milvusVectorStore;

    @Resource
    VectorizationFactory vectorizationFactory;

    @Test
    void contextLoads() throws IOException {

//        long start = System.currentTimeMillis();
//
//        String key = "eyJhbGciOiJIUzI1NiIsInNpZ25fdHlwZSI6IlNJR04ifQ.eyJhcGlfa2V5IjoiMmM5YWZkNjY4OTMzZDVmOWQ4MmJjNzRhNDVhZmViZGUiLCJleHAiOjE4Njg1MTE3MDYwMDAsInRpbWVzdGFtcCI6MTcxMDc0NTM3NjAwMH0.jFlrKDJNfX6pNh1_nqa-wJZb0TUFyLhJy5I-VAwhlHI";

        InputStream inputStream = new FileInputStream("C:\\Users\\chicr\\Documents\\java笔记\\面试指北\\面试指北.pdf");

        String content = resourseLoaderFactory.getLoader("pdf").getContent(inputStream);

        List<String> chunkList = resourseLoaderFactory.getLoader("pdf").getChunkList(content);
        System.out.println("chunkList.size() = " + chunkList.size());
        System.out.println(chunkList.getFirst());
        chunkList.forEach(System.out::println);
//        System.out.println("转换耗时: " + (System.currentTimeMillis()-start)/ 1000.0);
//
//        List<List<Double>> v = vectorizationFactory.getEmbedding("zhipu").doVectorization(chunkList,key);
//
//        milvusVectorStore.storeEmbeddings(chunkList,v,"javaGuide");
//        System.out.println("总耗时: " + (System.currentTimeMillis()-start)/ 1000.0);

//        List<Double> v2 =vectorizationFactory.getEmbedding("zhipu").singleVectorization("如何解决哈希冲突呢",key);;
//
//        List<String> w = milvusVectorStore.nearest(v2,"javaGuide");
//
//        w.forEach(System.out::println);

    }



}
