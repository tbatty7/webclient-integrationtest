package com.webclient.integrationtest.webclientintegrationtest;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WokeService {
    private WebClient webClient;

    public WokeService(WebClient wokeWebClient) {
        this.webClient = wokeWebClient;
    }

    public WokeResponse getAlarms() {
        return null;
    }
}