package com.hjong.achat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/
@Configuration
public class WebClientConfig {

    @Value("${chat.proxy.host}")
    private String host;

    @Value("${chat.proxy.port}")
    private int port;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public WebClient webClientEnableProxy(){

        HttpClient httpClient = HttpClient.create().proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP).host(host).port(port));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .build();
    }
}
