package com.hjong.achat;

import com.hjong.achat.service.LogService;
import com.hjong.achat.service.ServerService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import java.io.IOException;

@SpringBootTest
class AchatApplicationTests {


    @Resource
    R2dbcEntityTemplate r2dbcEntityTemplate;

    @Resource
    ServerService serverService;


    @Test
    void contextLoads() throws IOException, InterruptedException {


        serverService.status().subscribe(
                System.out::println
        );
        System.in.read();

    }



}
