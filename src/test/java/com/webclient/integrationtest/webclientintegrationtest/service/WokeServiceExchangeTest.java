package com.webclient.integrationtest.webclientintegrationtest.service;

import com.webclient.integrationtest.webclientintegrationtest.WokeResponse;
import com.webclient.integrationtest.webclientintegrationtest.service.WokeService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WokeServiceExchangeTest {
    @Mock
    private ExchangeFunction exchangeFunction;

    @Mock
    ClientResponse mockResponse;

    @Captor
    private ArgumentCaptor<ClientRequest> captor;

    private WokeService wokeService;

    @BeforeEach
    public void setup() {

        given(exchangeFunction.exchange(captor.capture())).willReturn(Mono.just(mockResponse));
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080").exchangeFunction(this.exchangeFunction).build();
        wokeService = new WokeService(webClient);
    }

    @Test
    void returnsResponseFromExternalService() {
        when(mockResponse.bodyToMono(WokeResponse.class)).thenReturn(Mono.just(buildWokeResponse()));

        WokeResponse response = wokeService.getAlarms();

        Assertions.assertThat(response).isEqualTo(buildWokeResponse());
    }

    @Test
    void urlPathIsCorrect() {
        when(mockResponse.bodyToMono(WokeResponse.class)).thenReturn(Mono.just(buildWokeResponse()));

        wokeService.getAlarms();

        ClientRequest clientRequest = verifyAndGetRequest();
        Assertions.assertThat(clientRequest.url().toString()).isEqualTo("http://localhost:8080/api/clock/alarms");
    }

    @Test
    void headerIsCorrect() {
        when(mockResponse.bodyToMono(WokeResponse.class)).thenReturn(Mono.just(buildWokeResponse()));

        wokeService.getAlarms();

        ClientRequest clientRequest = verifyAndGetRequest();
        Assertions.assertThat(clientRequest.headers().get("Identification-Id")).isEqualTo(Arrays.asList("1234"));
    }

    @Test
    void exceptionIsThrownOn500() {
        String errorResponse = "{\"errorCode\": \"We Have Bad Code\"}";
        when(mockResponse.bodyToMono(WokeResponse.class)).thenReturn(Mono.just(WokeResponse.builder().build()).handle((response, sink) ->
                sink.error(new WebClientResponseException(500, "", new HttpHeaders(), errorResponse.getBytes(), null))));

        assertThrows(WebClientResponseException.class, () -> wokeService.getAlarms());
    }

    private WokeResponse buildWokeResponse() {
        return WokeResponse.builder().alarm1("Wake up!").build();
    }

    private ClientRequest verifyAndGetRequest() {
        ClientRequest request = captor.getValue();
        verify(exchangeFunction).exchange(request);
        verifyNoMoreInteractions(exchangeFunction);
        return request;
    }
}