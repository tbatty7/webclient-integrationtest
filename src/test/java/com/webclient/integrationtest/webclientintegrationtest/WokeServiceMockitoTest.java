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

import static org.assertj.core.api.Assertions.assertThat;
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
    void returnsResponseFromExternalService() {
        Mockito.when(mockWebClient.get()).thenReturn(mockUriSpec);
        Mockito.when(mockUriSpec.uri(anyString())).thenReturn(mockHeadersSpec);
        Mockito.when(mockHeadersSpec.header(anyString(), anyString())).thenReturn(mockHeadersSpec);
        Mockito.when(mockHeadersSpec.retrieve()).thenReturn(mockResponseSpec);

        Mockito.when(mockResponseSpec.bodyToMono(WokeResponse.class)).thenReturn(Mono.just(buildResponseBody()));

        WokeResponse response = wokeService.getAlarms();
        assertThat(response).isEqualTo(buildResponseBody());
    }

    @Test
    void urlPathIsCorrect() {
        Mockito.when(mockWebClient.get()).thenReturn(mockUriSpec);
        Mockito.when(mockUriSpec.uri(anyString())).thenReturn(mockHeadersSpec);
        Mockito.when(mockHeadersSpec.header(anyString(), anyString())).thenReturn(mockHeadersSpec);
        Mockito.when(mockHeadersSpec.retrieve()).thenReturn(mockResponseSpec);

        Mockito.when(mockResponseSpec.bodyToMono(WokeResponse.class)).thenReturn(Mono.just(buildResponseBody()));

        wokeService.getAlarms();

        Mockito.verify(mockUriSpec).uri("/api/clock/alarms");
    }

    private WokeResponse buildResponseBody() {
        return WokeResponse.builder().build();
    }

    @Test
    void headerIsCorrect() {
        Mockito.when(mockWebClient.get()).thenReturn(mockUriSpec);
        Mockito.when(mockUriSpec.uri(anyString())).thenReturn(mockHeadersSpec);
        Mockito.when(mockHeadersSpec.header(anyString(), anyString())).thenReturn(mockHeadersSpec);
        Mockito.when(mockHeadersSpec.retrieve()).thenReturn(mockResponseSpec);

        Mockito.when(mockResponseSpec.bodyToMono(WokeResponse.class)).thenReturn(Mono.just(buildResponseBody()));

        wokeService.getAlarms();

        Mockito.verify(mockHeadersSpec).header("Identification-Id", "1234");
    }

    @Test
    void exceptionIsThrownOn500() {
        Mockito.when(mockWebClient.get()).thenReturn(mockUriSpec);
        Mockito.when(mockUriSpec.uri(anyString())).thenReturn(mockHeadersSpec);
        Mockito.when(mockHeadersSpec.header(anyString(), anyString())).thenReturn(mockHeadersSpec);
        Mockito.when(mockHeadersSpec.retrieve()).thenReturn(mockResponseSpec);

        String errorResponse = "{\"errorCode\": \"We Have Bad Code\"}";
        Mono<WokeResponse> errorResponseMono = Mono.just(buildResponseBody()).handle((response, sink) ->
                sink.error(new WebClientResponseException(500, "", new HttpHeaders(), errorResponse.getBytes(), null)));
        Mockito.when(mockResponseSpec.bodyToMono(WokeResponse.class)).thenReturn(errorResponseMono);

        Assertions.assertThrows(WebClientResponseException.class, () -> wokeService.getAlarms());

        Mockito.verify(mockHeadersSpec).header("Identification-Id", "1234");
    }

}