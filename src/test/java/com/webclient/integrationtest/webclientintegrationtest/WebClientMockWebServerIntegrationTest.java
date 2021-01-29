package com.webclient.integrationtest.webclientintegrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@SpringBootTest
@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("local")
@ContextConfiguration(classes = {WebclientIntegrationtestApplication.class, WebClientMockWebServerIntegrationTest.TestConfig.class})
class WebClientMockWebServerIntegrationTest {

	public static MockWebServer mockServer;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	JwtDecoder jwtDecoder;
//TODO: Add security that uses jwtDecoder

	@BeforeAll
	static void beforeAll() throws IOException {
		mockServer = new MockWebServer();
		mockServer.start();
	}

	@AfterAll
	static void afterAll() throws IOException {
		mockServer.shutdown();
	}

	@Configuration
	public static class TestConfig {
		@Bean(name = "wokeWebClient")
		WebClient dmppsWebClient() {
			HttpUrl url = mockServer.url("/");
			WebClient webClient = WebClient.create(url.toString());
			return webClient;
		}
	}
	@Test
	void successResponse() throws Exception {
		WokeResponse wokeResponse = WokeResponse.builder()
				.alarm1("Time to get up")
				.alarm2("You're gonna be late")
				.alarm3("Your boss is calling")
				.build();
		String responseBody = objectMapper.writeValueAsString(wokeResponse);
		mockBackendEndpoint(200, responseBody);
		ResultActions resultActions = executeRequest("1234");
		verifyResults(resultActions, 200);
	}
//TODO: add 500 scenario with Controller Advice

	private void mockBackendEndpoint(int responseCode, String body) {
		MockResponse mockResponse = new MockResponse().setResponseCode(responseCode)
				.setBody(body)
				.addHeader("Content-Type", "application/json");
		mockServer.enqueue(mockResponse);
	}

	private ResultActions executeRequest(String appId) throws Exception {
		return mockMvc.perform(MockMvcRequestBuilders
				.get("/v1/alarms")
				.header("Identification-No", appId)
				.header("Authorization", "Bearer 123"));
	}

	private void verifyResults(ResultActions resultActions, int status) throws Exception {
		resultActions
				.andDo(print())
				.andExpect(status().is(status));
		String responseBody = resultActions.andReturn().getResponse().getContentAsString();
		Assertions.assertThat(responseBody).contains("\"alarm1\":\"Time to get up\"");
		Assertions.assertThat(responseBody).contains("\"alarm2\":\"You're gonna be late\"");
	}
}
