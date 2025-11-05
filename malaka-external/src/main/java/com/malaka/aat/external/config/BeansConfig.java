package com.malaka.aat.external.config;


import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;


@Configuration
public class BeansConfig {


    @Bean
    public WebClient.Builder defaultBuilder() throws Exception{
        SslContext sslContext = SslContextBuilder
                .forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();

        HttpClient httpClient = HttpClient.create()
                .secure((spec) -> spec.sslContext(sslContext))
                .responseTimeout(Duration.ofSeconds(30));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }



}
