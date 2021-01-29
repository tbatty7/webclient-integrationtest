package com.webclient.integrationtest.webclientintegrationtest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean(name = "wokeWebClient")
    WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8880")
                .build();
    }

}