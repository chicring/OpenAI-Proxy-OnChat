package com.hjong.OnChat;

import com.hjong.OnChat.chain.loader.ResourseLoaderFactory;
import com.hjong.OnChat.chain.vectorizer.VectorizationFactory;
import com.hjong.OnChat.chain.vectorstore.MilvusVectorStore;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

//        InputStream inputStream = new FileInputStream("C:\\Users\\chicr\\Documents\\Java开发手册(黄山版).pdf");
//
//        String content = resourseLoaderFactory.getLoader("pdf").getContent(inputStream);
//
//        List<String> chunkList = resourseLoaderFactory.getLoader("pdf").getChunkList(content);
//        System.out.println("chunkList.size() = " + chunkList.size());
//        chunkList.forEach(s -> {
//            if (s.length() > 500) {
//                System.out.println(s);
//            }
//        });

        String s = "360gptjava";
        String[] split = s.split("-");
        System.out.printf(split[split.length - 1]);
//        InputStream inputStream = new FileInputStream("C:\\Users\\chicr\\Documents\\java笔记\\面试指北\\面试指北.md");
//
//        String content = resourseLoaderFactory.getLoader("markdown").getContent(inputStream);
//
//        List<String> chunkList = resourseLoaderFactory.getLoader("markdown").getChunkList(content);
//        System.out.println("chunkList.size() = " + chunkList.size());
//
//        AtomicInteger i = new AtomicInteger();
//
//        chunkList.forEach(s -> {
//            if (s.length() >= 800) {
//                System.out.println(s);
//                System.out.println("__________________________");
//
//                i.getAndIncrement();
//            }
////            System.out.println(s);
////            System.out.println("__________________________");
//        });
//        System.out.println("i = " + i);
//        System.out.println("chunkList.size() = " + chunkList.size());



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
