package com.webclient.integrationtest.webclientintegrationtest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class WokeServiceMockitoTest {
    @Mock
    private WebClient mockWebClient;

    @Mock
    WebClient.RequestHeadersUriSpec mockUriSpec;

    @Mock
    WebClient.RequestHeadersSpec mockHeadersSpec;

    @Mock
    WebClient.ResponseSpec mockResponseSpec;

    private WokeService wokeService;

    @BeforeEach
    void setUp() {
        wokeService = new WokeService(mockWebClient);
    }

    @Test
    void urlPathIsCorrect() {
        Mockito.when(mockWebClient.get()).thenReturn(mockUriSpec);
        Mockito.when(mockUriSpec.uri(anyString())).thenReturn(mockHeadersSpec);
        Mockito.when(mockHeadersSpec.header(anyString(), anyString())).thenReturn(mockHeadersSpec);
        Mockito.when(mockHeadersSpec.retrieve()).thenReturn(mockResponseSpec);

        Mockito.when(mockResponseSpec.bodyToMono(WokeResponse.class)).thenReturn(Mono.just(WokeResponse.builder().build()));

        wokeService.getAlarms();

        Mockito.verify(mockUriSpec).uri("/api/clock/alarms");
    }

    @Test
    void headerIsCorrect() {
        Mockito.when(mockWebClient.get()).thenReturn(mockUriSpec);
        Mockito.when(mockUriSpec.uri(anyString())).thenReturn(mockHeadersSpec);
        Mockito.when(mockHeadersSpec.header(anyString(), anyString())).thenReturn(mockHeadersSpec);
        Mockito.when(mockHeadersSpec.retrieve()).thenReturn(mockResponseSpec);

        Mockito.when(mockResponseSpec.bodyToMono(WokeResponse.class)).thenReturn(Mono.just(WokeResponse.builder().build()));

        wokeService.getAlarms();

        Mockito.verify(mockHeadersSpec).header("Identification-Id", "1234");
    }

    @Test
    void exceptionIsThrownOn400() {
        Mockito.when(mockWebClient.get()).thenReturn(mockUriSpec);
        Mockito.when(mockUriSpec.uri(anyString())).thenReturn(mockHeadersSpec);
        Mockito.when(mockHeadersSpec.header(anyString(), anyString())).thenReturn(mockHeadersSpec);
        Mockito.when(mockHeadersSpec.retrieve()).thenReturn(mockResponseSpec);

        String errorResponse = "{\"errorCode\": \"Bad Code\"}";
        Mono<WokeResponse> errorResponseMono = Mono.just(WokeResponse.builder().build()).handle((response, sink) ->
                sink.error(new WebClientResponseException(400, "", new HttpHeaders(), errorResponse.getBytes(), null)));
        Mockito.when(mockResponseSpec.bodyToMono(WokeResponse.class)).thenReturn(errorResponseMono);

        Assertions.assertThrows(WebClientResponseException.class, () -> wokeService.getAlarms());

        Mockito.verify(mockHeadersSpec).header("Identification-Id", "1234");
    }

}