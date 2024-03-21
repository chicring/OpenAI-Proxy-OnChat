package com.hjong.achat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableAsync
@EnableAspectJAutoProxy
public class AchatApplication {

    public static void main(String[] args) {
        SpringApplication.run(AchatApplication.class, args);
    }

}
