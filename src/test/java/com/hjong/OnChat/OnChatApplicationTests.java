package com.hjong.OnChat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hjong.OnChat.chain.loader.PdfFileLoader;
import com.hjong.OnChat.chain.loader.ResourceLoader;
import com.hjong.OnChat.chain.vectorizer.Vectorization;
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
    ModelRepository modelRepository;


    @Test
    void contextLoads() throws IOException {

//        Integer channelId = 14;
//        String models = "{ \"moonshot-v1-8k\":  \"moonshot-v1-8k\",\"moonshot-v1-32k\":  \"moonshot-v1-32k\",\"moonshot-v1-128k\":  \"moonshot-v1-128k\"}";
//        Map<String, String> map = JsonUtil.parseJSONArray(models, new TypeReference<Map<String, String>>() {});
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            // 创建新的Model对象
//            Model model = new Model();
//            model.setRequestModel(entry.getKey());
//            model.setRealModel(entry.getValue());
//            model.setChannelId(channelId);
//
//            // 插入到数据库中
//            modelRepository.save(model).subscribe(System.out::println);
//        }
//
//        System.in.read();
    }



}
