package com.hjong.achat;

import com.hjong.achat.entity.DTO.ApiKey;
import com.hjong.achat.entity.DTO.Logs;
import com.hjong.achat.repositories.ApiKeyRepositories;
import com.hjong.achat.repositories.ChannelRepositories;
import com.hjong.achat.repositories.LogsRepositories;
import com.hjong.achat.repositories.UserRepositories;
import com.hjong.achat.service.ApiKeyService;
import com.hjong.achat.service.ChannelService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;

import java.io.IOException;

@SpringBootTest
class AchatApplicationTests {


    @Resource
    R2dbcEntityTemplate r2dbcEntityTemplate;

    @Resource
    LogsRepositories  logsRepositories;


    @Test
    void contextLoads() throws IOException {

        Logs logs = new Logs();
        logs.setChannelId(1);
        logsRepositories.save(logs).subscribe(
                System.out::println
        );
        System.in.read();

    }

}
