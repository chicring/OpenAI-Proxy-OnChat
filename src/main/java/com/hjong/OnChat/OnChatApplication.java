package com.hjong.OnChat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;


//@EnableWebFlux
@SpringBootApplication
@EnableAsync
@EnableAspectJAutoProxy
public class OnChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnChatApplication.class, args);
    }

}
