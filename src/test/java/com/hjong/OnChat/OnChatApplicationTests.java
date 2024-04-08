package com.hjong.OnChat;

import com.hjong.OnChat.adapter.spark.SparkCompletions;
import com.hjong.OnChat.service.EmailService;
import com.hjong.OnChat.service.OverviewService;
import com.hjong.OnChat.util.CodeUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import java.time.Instant;

@SpringBootTest
class OnChatApplicationTests {


    @Resource
    SparkCompletions sparkCompletions;

    @Test
    void contextLoads() throws Exception {

        System.out.printf(sparkCompletions.getAuthUrl("https://spark-api.xf-yun.com/v3.5/chat", "18f83283ab018e7e001170745535099d", "YjJhMGQxMGY0NGExOWQ4OTAxZTU3MzYx"));

    }



}
