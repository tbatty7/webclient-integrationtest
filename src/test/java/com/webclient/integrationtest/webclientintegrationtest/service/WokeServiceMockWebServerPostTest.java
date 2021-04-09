package com.webclient.integrationtest.webclientintegrationtest.service;

import com.webclient.integrationtest.webclientintegrationtest.AlarmRequest;
import com.webclient.integrationtest.webclientintegrationtest.WokeResponse;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;


class WokeServiceMockWebServerPostTest {
    public static MockWebServer mockServer;
    private static WokeService wokeService;
    private static WebClient webClient;

    @BeforeAll
    static void setUpClass() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockServer.shutdown();
    }

    @BeforeEach
    void setUp() {
        HttpUrl url = mockServer.url("/");
        webClient = WebClient.create(url.toString());
        wokeService = new WokeService(webClient);
    }

    @Test
    void returnsResponseFromExternalService() {
        mockExternalEndpoint(200, "{\"alarm1\": \"Hello World\"}");
        WokeResponse response = wokeService.addAlarm(buildRequest());
        Assertions.assertThat(response).isEqualTo(WokeResponse.builder().alarm1("Hello World").build());
    }

    private AlarmRequest buildRequest() {
        return AlarmRequest.builder().message("Hello World").build();
    }

    private void mockExternalEndpoint(int responseCode, String body) {
        MockResponse mockResponse = new MockResponse().setResponseCode(responseCode)
                .setBody(body)
                .addHeader("Content-Type", "application/json");
        mockServer.enqueue(mockResponse);
    }
}